package uk.gov.companieshouse.authcode.changed.service;

import static org.mockito.ArgumentMatchers.any;

import java.util.List;
import java.util.function.Supplier;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;
import uk.gov.companieshouse.api.accounts.associations.model.Association;
import uk.gov.companieshouse.api.accounts.associations.model.AssociationsList;
import uk.gov.companieshouse.api.accounts.associations.model.RequestBodyPut.StatusEnum;
import uk.gov.companieshouse.api.handler.accountsassociation.request.PrivateAccountsAssociationForCompanyGet;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.authcode.cancellation.AuthCodeCancellation;
import uk.gov.companieshouse.authcode.changed.common.TestDataManager;

@ExtendWith(MockitoExtension.class)
@Tag("unit-test")
public class KafkaConsumerServiceTest {

    private static final TestDataManager testDataManager = TestDataManager.getInstance();

    @Mock
    private AssociationService associationService;

    @Mock
    private Acknowledgment acknowledgment;

    @InjectMocks
    private KafkaConsumerService kafkaConsumerService;

    private ConsumerRecord <String, AuthCodeCancellation> consumerRecord;

    @BeforeEach
    void setUp() {
        consumerRecord = new ConsumerRecord<>("authcode-cancellation", 1, 1L, "key", new AuthCodeCancellation("MICOMP001"));
    }

    @Test
    void consumerAuthCodeCancellationMessageSuccessfullyUpdatesStatus() {
        AssociationsList page = testDataManager.fetchAssociations("MiAssociation001" );
        Supplier<AssociationsList> fetchSupplier = () -> page;
        Supplier<String> updateSupplier = () -> "MiAssociation001";

        Mockito.doReturn( fetchSupplier ).when( associationService ).buildFetchAssociationsForCompanyRequest( "MICOMP001", false, 0, 15);
        Mockito.doReturn( updateSupplier ).when( associationService ).buildUpdateStatusRequest( "MiAssociation001", StatusEnum.UNAUTHORISED );
        kafkaConsumerService.consumeAuthCodeCancellationMessage( consumerRecord, 1, acknowledgment );

        Mockito.verify( associationService ).buildFetchAssociationsForCompanyRequest( "MICOMP001", false, 0, 15);
        Mockito.verify( associationService ).buildUpdateStatusRequest( "MiAssociation001", StatusEnum.UNAUTHORISED );

        Mockito.verify(acknowledgment ).acknowledge();
    }

    @Test
    void consumerAuthCodeCancellationMessageSuccessfullyUpdatesStatusWithPagination() {
        AssociationsList page = testDataManager.fetchAssociations("MiAssociation001" );
        Supplier<AssociationsList> fetchSupplier = () -> page;
        Supplier<String> updateSupplier = () -> "MiAssociation001";

        Mockito.doReturn( fetchSupplier ).when( associationService ).buildFetchAssociationsForCompanyRequest( "MICOMP001", false, 0, 15);
        Mockito.doReturn( updateSupplier ).when( associationService ).buildUpdateStatusRequest( "MiAssociation001", StatusEnum.UNAUTHORISED );
        kafkaConsumerService.consumeAuthCodeCancellationMessage( consumerRecord, 1, acknowledgment );

        Mockito.verify( associationService ).buildFetchAssociationsForCompanyRequest( "MICOMP001", false, 0, 15);
        Mockito.verify( associationService ).buildUpdateStatusRequest( "MiAssociation001", StatusEnum.UNAUTHORISED );

        Mockito.verify(acknowledgment ).acknowledge();
    }


    // 2 page
    // no associations found to update
    // exception thrown when fetching associations


}
