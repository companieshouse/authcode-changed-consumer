package uk.gov.companieshouse.authcode.changed.service;

import static uk.gov.companieshouse.authcode.changed.utils.LoggingUtil.LOGGER;

import consumer.exception.NonRetryableErrorException;
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

@Service
public class KafkaConsumerService {

    private static final double value = 2.0;
    private final AssociationService associationService;

    private static final Set<StatusEnum> updateableStatuses = Set.of( StatusEnum.CONFIRMED, StatusEnum.AWAITING_APPROVAL, StatusEnum.MIGRATED );

    public KafkaConsumerService(AssociationService associationService) {
        this.associationService = associationService;
    }


    @RetryableTopic(
            autoCreateTopics = "false",

            sameIntervalTopicReuseStrategy = SameIntervalTopicReuseStrategy.SINGLE_TOPIC,
            attempts = "${kafka.max-attempts}",
            backoff = @Backoff(
                    delayExpression = "${kafka.initial-backoff-delay}",
                    multiplier = value
            ),
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

    public void consumeAuthCodeCancellationMessage( final ConsumerRecord<String, AuthCodeCancellation> consumerRecord, final @Header( name = RetryTopicHeaders.DEFAULT_HEADER_ATTEMPTS, required = false ) Integer attemptNumber, final Acknowledgment acknowledgment ){
        final var companyNumber = consumerRecord.value().getCompanyNumber();
        final var xRequestId = String.format( "company_number: %s - Attempt:%d", companyNumber, attemptNumber );
        LOGGER.debugContext( xRequestId, "Received message", null );
        int pageIndex = 0;
        int totalPages;

        do {
            final var page = associationService.buildFetchAssociationsForCompanyRequest( companyNumber, false, pageIndex, 15 ).get();

            final var unsentRequests = page.getItems()
                    .stream()
                    .filter( association -> updateableStatuses.contains( association.getStatus() ) )
                    .map(Association::getId)
                    .map(id -> associationService.buildUpdateStatusRequest( id, RequestBodyPut.StatusEnum.UNAUTHORISED ) )
                    .toList();

            final var updatedAssociationIds = Flux.fromIterable( unsentRequests )
                    .flatMap( request -> Mono.just( request )
                            .map( Supplier::get ) )
                    .reduce( ( id1, id2 ) -> id1 + ", " + id2 )
                    .block();
            LOGGER.infoContext( xRequestId, String.format( "Updated association IDs %s", updatedAssociationIds ), null );

            acknowledgment.acknowledge();

            pageIndex++;
            totalPages = page.getTotalPages();
        } while ( pageIndex < totalPages );
    }
}