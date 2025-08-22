package uk.gov.companieshouse.authcode.changed.service;

import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpResponseException.Builder;
import java.util.function.Supplier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.associations.model.AssociationsList;
import uk.gov.companieshouse.api.accounts.associations.model.RequestBodyPut.StatusEnum;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.accountsassociation.request.PrivateAccountsAssociationForCompanyGet;
import uk.gov.companieshouse.api.handler.accountsassociation.request.PrivateAccountsAssociationUpdateStatusPatch;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.authcode.changed.common.Mockers;
import uk.gov.companieshouse.authcode.changed.exceptions.InternalServerErrorRuntimeException;
import uk.gov.companieshouse.authcode.changed.exceptions.NotFoundRuntimeException;
import uk.gov.companieshouse.authcode.changed.rest.AccountsAssociationEndpoint;

@ExtendWith(MockitoExtension.class)
@Tag("unit-test")
public class AssociationServiceTest {

    @Mock
    private AccountsAssociationEndpoint accountsAssociationEndpoint;

    @Mock
    private PrivateAccountsAssociationForCompanyGet privateAccountsAssociationForCompanyGet;

    @Mock
    private AssociationsList associationsList;

    @InjectMocks
    AssociationService associationService;

    private Mockers mockers;

    @BeforeEach
    void setup() {
        mockers = new Mockers(accountsAssociationEndpoint);
    }


    @Test
    void buildFetchAssociationForCompanyRequestWithNullInputReturnsInternalServerError() throws ApiErrorResponseException, URIValidationException {
        Mockito.doReturn( privateAccountsAssociationForCompanyGet ).when( accountsAssociationEndpoint ).buildGetAssociationsForCompanyRequest( null, false, 0, 1 );
        Mockito.doThrow( NullPointerException.class ).when( privateAccountsAssociationForCompanyGet ).execute();
        Assertions.assertThrows( InternalServerErrorRuntimeException.class, () -> associationService.buildFetchAssociationForCompanyRequest( null, false, 0, 1 ).get());
    }

    @Test
    void fetchUserDetailsWithMalformedInputReturnsInternalServerError() throws ApiErrorResponseException, URIValidationException {
        Mockito.doReturn( privateAccountsAssociationForCompanyGet ).when( accountsAssociationEndpoint ).buildGetAssociationsForCompanyRequest( "$$$", false, 0, 1 );
        Mockito.doThrow( new URIValidationException( "Uri incorrectly formatted" ) ).when( privateAccountsAssociationForCompanyGet ).execute();
        Assertions.assertThrows( InternalServerErrorRuntimeException.class, () -> associationService.buildFetchAssociationForCompanyRequest( "$$$", false, 0, 1 ).get());
    }

    @Test
    void fetchUserDetailsThrowsNotFoundForNonexistentUser() throws ApiErrorResponseException, URIValidationException {
        ApiErrorResponseException apiException = new ApiErrorResponseException( new ApiErrorResponseException.Builder( 404, "Not Found", new HttpHeaders() ) );
        Mockito.when( accountsAssociationEndpoint.buildGetAssociationsForCompanyRequest("111", false, 0, 1) ).thenReturn(privateAccountsAssociationForCompanyGet);
        Mockito.when( privateAccountsAssociationForCompanyGet.execute() ).thenThrow( apiException );
        Assertions.assertThrows( NotFoundRuntimeException.class, () -> associationService.buildFetchAssociationForCompanyRequest("111", false, 0, 1).get() );
    }

    @Test
    void fetchUserDetailsThrowsInternalServerErrorOnApiErrorWithNonNotFoundStatus() throws ApiErrorResponseException, URIValidationException {
        Mockito.doReturn( privateAccountsAssociationForCompanyGet ).when( accountsAssociationEndpoint ).buildGetAssociationsForCompanyRequest( "MKUser001", false, 0, 1 );
        Mockito.doThrow( new ApiErrorResponseException( new Builder( 500, "Something unexpected happened", new HttpHeaders() ) ) ).when( privateAccountsAssociationForCompanyGet ).execute();
        Supplier<AssociationsList> supplier = associationService.buildFetchAssociationForCompanyRequest( "MKUser001", false, 0, 1 );
        Assertions.assertThrows(InternalServerErrorRuntimeException.class, supplier::get);
    }

    @Test
    void fetchUserDetailsSuccessfullyFetchesUserData() throws ApiErrorResponseException, URIValidationException {
        mockers.mockGetAssociationDetails("333");
        Assertions.assertEquals("333", associationService.buildFetchAssociationForCompanyRequest("333", false, 0, 1)
                .get()
                .getItems()
                .getFirst()
                .getCompanyNumber());
    }

    @Test
    void createUpdateStatusSuccessfullyUpdatesStatus() throws ApiErrorResponseException, URIValidationException {
        final var associationId = "MKAssociation001";
        final StatusEnum status = StatusEnum.UNAUTHORISED;
        mockers.mockUpdateStatusSuccess(associationId, status);
        String result = associationService.createUpdateStatusRequest(associationId, status).get();
        Assertions.assertEquals(associationId, result);
        Mockito.verify(accountsAssociationEndpoint).createUpdateStatusRequest(associationId, status);
    }

    @Test
    void createUpdateStatusThrowsNotFoundWhenAssociationDoesNotExist() throws ApiErrorResponseException, URIValidationException {
        mockers.mockUpdateStatusNotFound("MKAssociation001", StatusEnum.UNAUTHORISED);
        Assertions.assertThrows(NotFoundRuntimeException.class, () -> associationService.createUpdateStatusRequest("MKAssociation001", StatusEnum.UNAUTHORISED).get());
    }

    @Test
    void createUpdateStatusThrowsInternalServerErrorOnUnexpectedApiError() throws ApiErrorResponseException, URIValidationException {
        final String associationId = "MKAssociation001";
        final StatusEnum status = StatusEnum.UNAUTHORISED;
        final PrivateAccountsAssociationUpdateStatusPatch mockPrivateAccountsAssociationUpdateStatusPatch = Mockito.mock(PrivateAccountsAssociationUpdateStatusPatch.class);
        Mockito.when( accountsAssociationEndpoint.createUpdateStatusRequest( associationId, status ) ).thenReturn( mockPrivateAccountsAssociationUpdateStatusPatch );
        ApiErrorResponseException apiException = new ApiErrorResponseException( new ApiErrorResponseException.Builder( 500, "Error", new HttpHeaders() ) );

        Mockito.doThrow( apiException ).when( mockPrivateAccountsAssociationUpdateStatusPatch ).execute();
        Assertions.assertThrows( InternalServerErrorRuntimeException.class, () -> associationService.createUpdateStatusRequest( associationId, status ).get() );
    }

    @Test
    void createUpdateStatusThrowsInternalServerErrorOnUnexpectedException() throws ApiErrorResponseException, URIValidationException {
        final var associationId = "MKAssociation001";
        final var status = StatusEnum.UNAUTHORISED;
        final PrivateAccountsAssociationUpdateStatusPatch mockPrivateAccountsAssociationUpdateStatusPatch = Mockito.mock( PrivateAccountsAssociationUpdateStatusPatch.class );
        Mockito.when( accountsAssociationEndpoint.createUpdateStatusRequest( associationId, status ) ).thenReturn(mockPrivateAccountsAssociationUpdateStatusPatch);

        Mockito.doThrow( new NullPointerException( "Unexpected null" ) ).when( mockPrivateAccountsAssociationUpdateStatusPatch ).execute();
        Assertions.assertThrows( InternalServerErrorRuntimeException.class, () -> associationService.createUpdateStatusRequest( associationId, status ).get());
    }

}