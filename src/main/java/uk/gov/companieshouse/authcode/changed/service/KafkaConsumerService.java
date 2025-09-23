package uk.gov.companieshouse.authcode.changed.service;

import static uk.gov.companieshouse.authcode.changed.utils.LoggingUtil.LOGGER;

import consumer.exception.NonRetryableErrorException;
import java.time.Duration;
import java.util.Set;
import java.util.function.Supplier;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.retrytopic.RetryTopicHeaders;
import org.springframework.kafka.retrytopic.SameIntervalTopicReuseStrategy;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import uk.gov.companieshouse.api.accounts.associations.model.Association;
import uk.gov.companieshouse.api.accounts.associations.model.Association.StatusEnum;
import uk.gov.companieshouse.api.accounts.associations.model.RequestBodyPut;
import uk.gov.companieshouse.authcode.cancellation.AuthCodeCancellation;
import uk.gov.companieshouse.authcode.changed.utils.StaticPropertyUtil;

@Service
public class KafkaConsumerService {
    
    private static final Set<StatusEnum> updateableStatuses = Set.of( StatusEnum.CONFIRMED, StatusEnum.AWAITING_APPROVAL, StatusEnum.MIGRATED );

    private final AssociationService associationService;

    public KafkaConsumerService(final AssociationService associationService) {
        this.associationService = associationService;
    }


    @RetryableTopic(
            autoCreateTopics = "false",
            sameIntervalTopicReuseStrategy = SameIntervalTopicReuseStrategy.SINGLE_TOPIC,
            attempts = "${kafka.max-attempts}",
            backoff = @Backoff(delayExpression = "${kafka.backoff-delay}"),
            exclude = NonRetryableErrorException.class,
            kafkaTemplate = "kafkaAuthCodeCancellationTemplate",
            dltTopicSuffix = "-error",
            dltStrategy = DltStrategy.ALWAYS_RETRY_ON_ERROR
    )
    @KafkaListener(
            topics = {"${kafka.topic.authcode.cancellation}"},
            groupId = "${kafka.group-id.authcode.cancellation}",
            containerFactory = "listenerContainerFactoryAuthCodeCancellation"
    )
    public void consumeAuthCodeCancellationMessage( final ConsumerRecord<String, AuthCodeCancellation> consumerRecord,
            final @Header( name = RetryTopicHeaders.DEFAULT_HEADER_ATTEMPTS, required = false ) Integer attemptNumber,
            final Acknowledgment acknowledgment ){
        final var companyNumber = consumerRecord.value().getCompanyNumber();
        final var xRequestId = String.format( "company_number: %s - Attempt:%d", companyNumber, attemptNumber );
        LOGGER.debugContext( xRequestId, "Received message", null );

        try {
            int pageIndex = 0;
            int totalPages;
            int itemsPerPage = StaticPropertyUtil.KAFKA_ASSOCIATION_ITEMS_PER_PAGE > 0 ? StaticPropertyUtil.KAFKA_ASSOCIATION_ITEMS_PER_PAGE : 15;

            do {
                LOGGER.debugContext( xRequestId, String.format( "Attempting to retrieve associations for company %s", companyNumber ), null );
                final var page = associationService.buildFetchAssociationsForCompanyRequest( companyNumber, false, pageIndex, itemsPerPage).get();

                LOGGER.debugContext( xRequestId, "Preparing PATCH requests", null );
                final var unsentRequests = page.getItems()
                        .stream()
                        .filter( association -> updateableStatuses.contains( association.getStatus() ) )
                        .map(Association::getId)
                        .map(id -> associationService.buildUpdateStatusRequest( id, RequestBodyPut.StatusEnum.UNAUTHORISED ) )
                        .toList();

                LOGGER.debugContext( xRequestId, "Sending PATCH requests", null );
                final var updatedAssociationIds = Flux.fromIterable( unsentRequests )
                        .flatMap( request -> Mono.just( request )
                                .map( Supplier::get ) )
                        .reduce( ( id1, id2 ) -> id1 + ", " + id2 )
                        .block(Duration.ofSeconds(20));
                LOGGER.infoContext( xRequestId, String.format( "Updated association IDs %s", updatedAssociationIds ), null );

                acknowledgment.acknowledge();

                pageIndex++;
                totalPages = page.getTotalPages();
            } while ( pageIndex < totalPages );
        } catch ( Exception exception ){
            LOGGER.errorContext( xRequestId, exception, null );
            throw exception;
        }

    }
}