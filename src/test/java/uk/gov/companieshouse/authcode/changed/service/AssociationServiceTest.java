package uk.gov.companieshouse.authcode.changed.service;

import static org.mockito.Mockito.when;

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
    void buildFetchAssociationsForCompanyRequestWithNullInputReturnsInternalServerError() throws ApiErrorResponseException, URIValidationException {
        Mockito.doReturn( privateAccountsAssociationForCompanyGet ).when( accountsAssociationEndpoint ).buildGetAssociationsForCompanyRequest( null, false, 0, 1 );
        Mockito.doThrow( NullPointerException.class ).when( privateAccountsAssociationForCompanyGet ).execute();
        Assertions.assertThrows( InternalServerErrorRuntimeException.class, () -> associationService.buildFetchAssociationsForCompanyRequest( null, false, 0, 1 ).get());
    }

    @Test
    void fetchUserDetailsWithMalformedInputReturnsInternalServerError() throws ApiErrorResponseException, URIValidationException {
        Mockito.doReturn( privateAccountsAssociationForCompanyGet ).when( accountsAssociationEndpoint ).buildGetAssociationsForCompanyRequest( "$$$", false, 0, 1 );
        Mockito.doThrow( new URIValidationException( "Uri incorrectly formatted" ) ).when( privateAccountsAssociationForCompanyGet ).execute();
        Assertions.assertThrows( InternalServerErrorRuntimeException.class, () -> associationService.buildFetchAssociationsForCompanyRequest( "$$$", false, 0, 1 ).get());
    }

    @Test
    void fetchUserDetailsThrowsNotFoundForNonexistentUser() throws ApiErrorResponseException, URIValidationException {
        ApiErrorResponseException apiException = new ApiErrorResponseException( new ApiErrorResponseException.Builder( 404, "Not Found", new HttpHeaders() ) );
        when( accountsAssociationEndpoint.buildGetAssociationsForCompanyRequest("111", false, 0, 1) ).thenReturn(privateAccountsAssociationForCompanyGet);
        when( privateAccountsAssociationForCompanyGet.execute() ).thenThrow( apiException );
        Assertions.assertThrows( NotFoundRuntimeException.class, () -> associationService.buildFetchAssociationsForCompanyRequest("111", false, 0, 1).get() );
    }

    @Test
    void fetchUserDetailsThrowsInternalServerErrorOnApiErrorWithNonNotFoundStatus() throws ApiErrorResponseException, URIValidationException {
        Mockito.doReturn( privateAccountsAssociationForCompanyGet ).when( accountsAssociationEndpoint ).buildGetAssociationsForCompanyRequest( "MKUser001", false, 0, 1 );
        Mockito.doThrow( new ApiErrorResponseException( new Builder( 500, "Something unexpected happened", new HttpHeaders() ) ) ).when( privateAccountsAssociationForCompanyGet ).execute();
        Supplier<AssociationsList> supplier = associationService.buildFetchAssociationsForCompanyRequest( "MKUser001", false, 0, 1 );
        Assertions.assertThrows(InternalServerErrorRuntimeException.class, supplier::get);
    }

    @Test
    void fetchUserDetailsSuccessfullyFetchesUserData() throws ApiErrorResponseException, URIValidationException {
        mockers.mockGetAssociationDetails("MiAssociation001");
        var result = associationService.buildFetchAssociationsForCompanyRequest("MICOMP001", false, 0, 1)
                .get()
                .getItems()
                .getFirst()
                .getCompanyNumber();

        Assertions.assertEquals("MICOMP001", result);
        Mockito.verify(accountsAssociationEndpoint).buildGetAssociationsForCompanyRequest("MICOMP001", false, 0, 1);
    }

    @Test
    void createUpdateStatusSuccessfullyUpdatesStatus() throws ApiErrorResponseException, URIValidationException {
        final var associationId = "MKAssociation001";
        final StatusEnum status = StatusEnum.UNAUTHORISED;
        mockers.mockUpdateStatusSuccess(associationId, status);
        String result = associationService.buildUpdateStatusRequest(associationId, status).get();
        Assertions.assertEquals(associationId, result);
        Mockito.verify(accountsAssociationEndpoint).buildUpdateStatusRequest(associationId, status);
    }

    @Test
    void createUpdateStatusThrowsNotFoundWhenAssociationDoesNotExist() throws ApiErrorResponseException, URIValidationException {
        mockers.mockUpdateStatusNotFound("MKAssociation001", StatusEnum.UNAUTHORISED);
        Assertions.assertThrows(NotFoundRuntimeException.class, () -> associationService.buildUpdateStatusRequest("MKAssociation001", StatusEnum.UNAUTHORISED).get());
    }

    @Test
    void createUpdateStatusThrowsInternalServerErrorOnUnexpectedApiError() throws ApiErrorResponseException, URIValidationException {
        final String associationId = "MKAssociation001";
        final StatusEnum status = StatusEnum.UNAUTHORISED;
        final PrivateAccountsAssociationUpdateStatusPatch mockPrivateAccountsAssociationUpdateStatusPatch = Mockito.mock(PrivateAccountsAssociationUpdateStatusPatch.class);
        when( accountsAssociationEndpoint.buildUpdateStatusRequest( associationId, status ) ).thenReturn( mockPrivateAccountsAssociationUpdateStatusPatch );
        ApiErrorResponseException apiException = new ApiErrorResponseException( new ApiErrorResponseException.Builder( 500, "Error", new HttpHeaders() ) );

        Mockito.doThrow( apiException ).when( mockPrivateAccountsAssociationUpdateStatusPatch ).execute();
        Assertions.assertThrows( InternalServerErrorRuntimeException.class, () -> associationService.buildUpdateStatusRequest( associationId, status ).get() );
    }

    @Test
    void createUpdateStatusThrowsInternalServerErrorOnUnexpectedException() throws ApiErrorResponseException, URIValidationException {
        final var associationId = "MKAssociation001";
        final var status = StatusEnum.UNAUTHORISED;
        final PrivateAccountsAssociationUpdateStatusPatch mockPrivateAccountsAssociationUpdateStatusPatch = Mockito.mock( PrivateAccountsAssociationUpdateStatusPatch.class );
        when( accountsAssociationEndpoint.buildUpdateStatusRequest( associationId, status ) ).thenReturn(mockPrivateAccountsAssociationUpdateStatusPatch);

        Mockito.doThrow( new NullPointerException( "Unexpected null" ) ).when( mockPrivateAccountsAssociationUpdateStatusPatch ).execute();
        Assertions.assertThrows( InternalServerErrorRuntimeException.class, () -> associationService.buildUpdateStatusRequest( associationId, status ).get());
    }

}