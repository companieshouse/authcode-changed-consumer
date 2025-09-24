package uk.gov.companieshouse.authcode.changed.service;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;

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
import uk.gov.companieshouse.api.accounts.associations.model.AssociationsList;
import uk.gov.companieshouse.api.accounts.associations.model.RequestBodyPut.StatusEnum;
import uk.gov.companieshouse.authcode.cancellation.AuthCodeCancellation;
import uk.gov.companieshouse.authcode.changed.common.TestDataManager;

@ExtendWith(MockitoExtension.class)
@Tag("unit-test")
class KafkaConsumerServiceTest {

    private static final TestDataManager testDataManager = TestDataManager.getInstance();

    @Mock
    private AssociationService associationService;

    @Mock
    private Acknowledgment acknowledgment;

    @InjectMocks
    private KafkaConsumerService kafkaConsumerService;

    private ConsumerRecord <String, AuthCodeCancellation> consumerRecord1;
    private ConsumerRecord <String, AuthCodeCancellation> consumerRecord2;

    @BeforeEach
    void setUp() {
        consumerRecord1 = new ConsumerRecord<>("authcode-cancellation", 1, 1L, "key", new AuthCodeCancellation("MICOMP001"));
        consumerRecord2 = new ConsumerRecord<>("authcode-cancellation", 1, 1L, "key", new AuthCodeCancellation("MICOMP002"));
    }

    @Test
    void consumerAuthCodeCancellationMessageSuccessfullyUpdatesStatus() {
        AssociationsList page = testDataManager.fetchAssociations(15, 0, 1, "MiAssociation001" );
        Supplier<AssociationsList> fetchSupplier = () -> page;
        Supplier<String> updateSupplier = () -> "MiAssociation001";

        Mockito.doReturn( fetchSupplier ).when( associationService ).buildFetchAssociationsForCompanyRequest( anyString(), eq("MICOMP001"), eq(false), eq(0), anyInt());
        Mockito.doReturn( updateSupplier ).when( associationService ).buildUpdateStatusRequest( anyString(),eq( "MiAssociation001" ), eq( StatusEnum.UNAUTHORISED ) );
        kafkaConsumerService.consumeAuthCodeCancellationMessage(consumerRecord1, 1, acknowledgment );

        Mockito.verify( associationService ).buildFetchAssociationsForCompanyRequest( anyString(), eq("MICOMP001"), eq(false), eq(0), anyInt());
        Mockito.verify( associationService ).buildUpdateStatusRequest( anyString(),eq( "MiAssociation001" ), eq( StatusEnum.UNAUTHORISED ) );

        Mockito.verify(acknowledgment ).acknowledge();
    }

    @Test
    void consumerAuthCodeCancellationMessageSuccessfullyUpdatesStatusWith4Pages1ItemPerPage() {
        AssociationsList page1 = testDataManager.fetchAssociations(1, 0, 2, "MiAssociation001");
        AssociationsList page2 = testDataManager.fetchAssociations(1, 1, 2, "MiAssociation002");
        AssociationsList page3 = testDataManager.fetchAssociations(1, 0, 2, "MiAssociation039");
        AssociationsList page4 = testDataManager.fetchAssociations(1, 1, 2, "MiAssociation040");
        Supplier<AssociationsList> fetchSupplier1 = () -> page1;
        Supplier<AssociationsList> fetchSupplier2 = () -> page2;
        Supplier<AssociationsList> fetchSupplier3 = () -> page3;
        Supplier<AssociationsList> fetchSupplier4 = () -> page4;
        Supplier<String> UpdateSupplier1 = () -> "MiAssociation001";
        Supplier<String> UpdateSupplier2 = () -> "MiAssociation002";
        Supplier<String> UpdateSupplier3 = () -> "MiAssociation0044";
        Supplier<String> UpdateSupplier4 = () -> "MiAssociation0045";

        Mockito.doReturn( fetchSupplier1 ).when( associationService ).buildFetchAssociationsForCompanyRequest( anyString(), eq("MICOMP001"), eq(false), eq(0), anyInt());
        Mockito.doReturn( fetchSupplier2 ).when( associationService ).buildFetchAssociationsForCompanyRequest( anyString(), eq("MICOMP001"), eq(false), eq(1), anyInt());
        Mockito.doReturn( fetchSupplier3 ).when( associationService ).buildFetchAssociationsForCompanyRequest( anyString(), eq("MICOMP002"), eq(false), eq(0), anyInt());
        Mockito.doReturn( fetchSupplier4 ).when( associationService ).buildFetchAssociationsForCompanyRequest( anyString(), eq("MICOMP002"), eq(false), eq(1), anyInt());
        Mockito.doReturn( UpdateSupplier1 ).when( associationService ).buildUpdateStatusRequest( anyString(),eq( "MiAssociation001" ), eq( StatusEnum.UNAUTHORISED ) );
        Mockito.doReturn( UpdateSupplier2 ).when( associationService ).buildUpdateStatusRequest( anyString(), eq( "MiAssociation002" ), eq( StatusEnum.UNAUTHORISED ) );
        Mockito.doReturn( UpdateSupplier3 ).when( associationService ).buildUpdateStatusRequest( anyString(), eq( "MiAssociation039" ), eq( StatusEnum.UNAUTHORISED ) );
        Mockito.doReturn( UpdateSupplier4 ).when( associationService ).buildUpdateStatusRequest( anyString(), eq( "MiAssociation040" ), eq( StatusEnum.UNAUTHORISED ) );

        kafkaConsumerService.consumeAuthCodeCancellationMessage(consumerRecord1, 1, acknowledgment );
        kafkaConsumerService.consumeAuthCodeCancellationMessage(consumerRecord2, 1, acknowledgment );

        Mockito.verify( associationService ).buildFetchAssociationsForCompanyRequest( anyString(), eq("MICOMP001"), eq(false), eq(0), anyInt());
        Mockito.verify( associationService ).buildFetchAssociationsForCompanyRequest( anyString(), eq("MICOMP001"), eq(false), eq(1), anyInt());
        Mockito.verify( associationService ).buildFetchAssociationsForCompanyRequest( anyString(), eq("MICOMP002"), eq(false), eq(0), anyInt());
        Mockito.verify( associationService ).buildFetchAssociationsForCompanyRequest( anyString(), eq("MICOMP002"), eq(false), eq(1), anyInt());
        Mockito.verify( associationService ).buildUpdateStatusRequest( anyString(),eq( "MiAssociation001" ), eq( StatusEnum.UNAUTHORISED ) );
        Mockito.verify( associationService ).buildUpdateStatusRequest( anyString(),eq( "MiAssociation002" ), eq( StatusEnum.UNAUTHORISED ) );

        Mockito.verify(acknowledgment, times(4) ).acknowledge();
    }

    @Test
    void consumerAuthCodeCancellationMessageSuccessfullyUpdatesStatusWith2Pages3ItemsPerPage() {
        AssociationsList page1 = testDataManager.fetchAssociations(3, 0, 2, "MiAssociation001", "MiAssociation002", "MiAssociation012");
        AssociationsList page2 = testDataManager.fetchAssociations(3, 1, 2, "MiAssociation014", "MiAssociation015", "MiAssociation016");
        Supplier<AssociationsList> fetchSupplier1 = () -> page1;
        Supplier<AssociationsList> fetchSupplier2 = () -> page2;
        Supplier<String> UpdateSupplier1 = () -> "MiAssociation001";
        Supplier<String> UpdateSupplier2 = () -> "MiAssociation002";
        Supplier<String> UpdateSupplier3 = () -> "MiAssociation003";
        Supplier<String> UpdateSupplier4 = () -> "MiAssociation004";
        Supplier<String> UpdateSupplier5 = () -> "MiAssociation005";
        Supplier<String> UpdateSupplier6 = () -> "MiAssociation006";

        Mockito.doReturn( fetchSupplier1 ).when( associationService ).buildFetchAssociationsForCompanyRequest( anyString(),eq( "MICOMP001" ), eq( false ), eq( 0 ), eq( 15 ) );
        Mockito.doReturn( fetchSupplier2 ).when( associationService ).buildFetchAssociationsForCompanyRequest( anyString(),eq( "MICOMP001" ), eq( false ), eq( 1 ), eq( 15 ) );

        Mockito.doReturn( UpdateSupplier1 ).when( associationService ).buildUpdateStatusRequest( anyString(),eq( "MiAssociation001" ), eq( StatusEnum.UNAUTHORISED ) );
        Mockito.doReturn( UpdateSupplier2 ).when( associationService ).buildUpdateStatusRequest( anyString(),eq( "MiAssociation002" ), eq( StatusEnum.UNAUTHORISED ) );
        Mockito.doReturn( UpdateSupplier3 ).when( associationService ).buildUpdateStatusRequest( anyString(),eq( "MiAssociation012" ), eq( StatusEnum.UNAUTHORISED ) );
        Mockito.doReturn( UpdateSupplier4 ).when( associationService ).buildUpdateStatusRequest( anyString(),eq( "MiAssociation014" ), eq( StatusEnum.UNAUTHORISED ) );
        Mockito.doReturn( UpdateSupplier5 ).when( associationService ).buildUpdateStatusRequest( anyString(),eq( "MiAssociation015" ), eq( StatusEnum.UNAUTHORISED ) );
        Mockito.doReturn( UpdateSupplier6 ).when( associationService ).buildUpdateStatusRequest( anyString(),eq( "MiAssociation016" ), eq( StatusEnum.UNAUTHORISED ) );

        kafkaConsumerService.consumeAuthCodeCancellationMessage(consumerRecord1, 1, acknowledgment );

        Mockito.verify( associationService ).buildFetchAssociationsForCompanyRequest( anyString(),eq( "MICOMP001" ), eq( false ), eq( 0 ), eq( 15 ) );
        Mockito.verify( associationService ).buildFetchAssociationsForCompanyRequest( anyString(),eq( "MICOMP001" ), eq( false ), eq( 1 ), eq( 15 ) );

        Mockito.verify( associationService ).buildUpdateStatusRequest( anyString(),eq( "MiAssociation001" ), eq( StatusEnum.UNAUTHORISED ) );
        Mockito.verify( associationService ).buildUpdateStatusRequest( anyString(),eq( "MiAssociation002" ), eq( StatusEnum.UNAUTHORISED ) );
        Mockito.verify( associationService ).buildUpdateStatusRequest( anyString(),eq( "MiAssociation012" ), eq( StatusEnum.UNAUTHORISED ) );
        Mockito.verify( associationService ).buildUpdateStatusRequest( anyString(),eq( "MiAssociation014" ), eq( StatusEnum.UNAUTHORISED ) );
        Mockito.verify( associationService ).buildUpdateStatusRequest( anyString(),eq( "MiAssociation015" ), eq( StatusEnum.UNAUTHORISED ) );
        Mockito.verify( associationService ).buildUpdateStatusRequest( anyString(),eq( "MiAssociation016" ), eq( StatusEnum.UNAUTHORISED ) );

        Mockito.verify(acknowledgment, times(2) ).acknowledge();
    }
}
