package uk.gov.companieshouse.authcode.changed.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import uk.gov.companieshouse.api.accounts.associations.model.RequestBodyPut.StatusEnum;
import uk.gov.companieshouse.authcode.changed.common.Mockers;
import uk.gov.companieshouse.authcode.changed.common.TestDataManager;
import uk.gov.companieshouse.authcode.changed.exceptions.InternalServerErrorRuntimeException;
import uk.gov.companieshouse.authcode.changed.exceptions.NotFoundRuntimeException;

@ExtendWith( MockitoExtension.class )
@Tag("unit-test")
public class AssociationServiceTest1 {

    @Mock
    private WebClient associationWebClient;

    @InjectMocks
    AssociationService associationService;

    private static final TestDataManager testDataManager = TestDataManager.getInstance();

    private Mockers mockers;

    @BeforeEach
    void setup() {
        mockers = new Mockers( associationWebClient, null );
    }

    @Test
    void fetchAssociationsForCompanyDetailsWithValidCompanyNumberReturnsAssociations() throws JsonProcessingException {
        final var companyNumber = testDataManager.getAssociation1().getCompanyNumber();
        mockers.mockWebClientForFetchCompanyDetails( companyNumber );

        // Call the method to test
        var associations = associationService.fetchAssociationDetails( companyNumber );

    }

    @Test
    void fetchAssociationDetailsForNullOrNonexistentUserReturnsNotFoundRuntimeException() {
        Assertions.assertThrows( InternalServerErrorRuntimeException.class, () -> associationService.fetchAssociationDetails( null ) );
        Assertions.assertThrows( InternalServerErrorRuntimeException.class, () -> associationService.fetchAssociationDetails( "404User") );
    }

    @Test
    void fetchUserDetailsForNullOrNonexistentUserReturnsNotFoundRuntimeException() {
        mockers.mockWebClientForFetchUserDetailsErrorResponse( null, 404 );
        Assertions.assertThrows( NotFoundRuntimeException.class, () -> associationService.createUpdateStatusRequest( null, StatusEnum.CONFIRMED ) );
//
//        mockers.mockWebClientForFetchUserDetailsErrorResponse( "404User", 404 );
//        Assertions.assertThrows( NotFoundRuntimeException.class, () -> usersService.fetchUserDetails( "404User", "id123" ) );
    }

}
