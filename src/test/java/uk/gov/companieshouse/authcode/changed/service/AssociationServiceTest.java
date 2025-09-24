package uk.gov.companieshouse.authcode.changed.service;

import static org.mockito.Mockito.when;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpResponseException.Builder;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.associations.model.Association;
import uk.gov.companieshouse.api.accounts.associations.model.AssociationsList;
import uk.gov.companieshouse.api.accounts.associations.model.RequestBodyPut.StatusEnum;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.accountsassociation.request.PrivateAccountsAssociationForCompanyGet;
import uk.gov.companieshouse.api.handler.accountsassociation.request.PrivateAccountsAssociationUpdateStatusPatch;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.authcode.changed.common.TestDataManager;
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

    @InjectMocks
    AssociationService associationService;

    private static final TestDataManager testDataManager = TestDataManager.getInstance();

    @Test
    void fetchAssociationsForCompanyRequestWithNullInputReturnsInternalServerError() throws ApiErrorResponseException, URIValidationException {
        Mockito.doReturn( privateAccountsAssociationForCompanyGet ).when( accountsAssociationEndpoint ).buildGetAssociationsForCompanyRequest( "theId123",null, false, 0, 1 );
        Mockito.doThrow( NullPointerException.class ).when( privateAccountsAssociationForCompanyGet ).execute();
        Assertions.assertThrows( InternalServerErrorRuntimeException.class, () -> associationService.buildFetchAssociationsForCompanyRequest( "theId123",null, false, 0, 1 ).get());
    }

    @Test
    void fetchAssociationDetailsWithMalformedInputReturnsInternalServerError() throws ApiErrorResponseException, URIValidationException {
        Mockito.doReturn( privateAccountsAssociationForCompanyGet ).when( accountsAssociationEndpoint ).buildGetAssociationsForCompanyRequest( "theId123","$$$", false, 0, 1 );
        Mockito.doThrow( new URIValidationException( "Uri incorrectly formatted" ) ).when( privateAccountsAssociationForCompanyGet ).execute();
        Assertions.assertThrows( InternalServerErrorRuntimeException.class, () -> associationService.buildFetchAssociationsForCompanyRequest( "theId123","$$$", false, 0, 1 ).get());
    }

    @Test
    void fetchAssociationDetailsWithNonexistentAssociationReturnsNotFound() throws ApiErrorResponseException, URIValidationException {
        ApiErrorResponseException apiException = new ApiErrorResponseException( new ApiErrorResponseException.Builder( 404, "Not Found", new HttpHeaders() ) );
        when( accountsAssociationEndpoint.buildGetAssociationsForCompanyRequest("theId123","111", false, 0, 1) ).thenReturn(privateAccountsAssociationForCompanyGet);
        when( privateAccountsAssociationForCompanyGet.execute() ).thenThrow( apiException );
        Assertions.assertThrows( NotFoundRuntimeException.class, () -> associationService.buildFetchAssociationsForCompanyRequest("theId123","111", false, 0, 1).get() );
    }

    @Test
    void fetchAssociationDetailsWithUnexpectedErrorReturnsInternalServerError() throws ApiErrorResponseException, URIValidationException {
        Mockito.doReturn( privateAccountsAssociationForCompanyGet ).when( accountsAssociationEndpoint ).buildGetAssociationsForCompanyRequest( "theId123","MKUser001", false, 0, 1 );
        Mockito.doThrow( new ApiErrorResponseException( new Builder( 500, "Something unexpected happened", new HttpHeaders() ) ) ).when( privateAccountsAssociationForCompanyGet ).execute();
        Supplier<AssociationsList> supplier = associationService.buildFetchAssociationsForCompanyRequest( "theId123","MKUser001", false, 0, 1 );
        Assertions.assertThrows(InternalServerErrorRuntimeException.class, supplier::get);
    }

    @Test
    void fetchAssociationDetailsReturnsSuccessfully() throws ApiErrorResponseException, URIValidationException {
        Association association = testDataManager.fetchAssociation("MiAssociation001").getFirst();
        AssociationsList associationsList = new AssociationsList();
        associationsList.setItems(List.of(association));

        PrivateAccountsAssociationForCompanyGet request = Mockito.mock( PrivateAccountsAssociationForCompanyGet.class );
        Mockito.doReturn( request ).when( accountsAssociationEndpoint ).buildGetAssociationsForCompanyRequest( "theId123","MICOMP001", false, 0, 1 );
        Mockito.doReturn(new ApiResponse<>(200, Map.of(), associationsList)).when(request).execute();

        var result = associationService.buildFetchAssociationsForCompanyRequest( "theId123","MICOMP001", false, 0, 1 ).get().getItems().getFirst().getCompanyNumber();

        Assertions.assertEquals( "MICOMP001", result );
        Mockito.verify( accountsAssociationEndpoint ).buildGetAssociationsForCompanyRequest( "theId123", "MICOMP001", false, 0, 1 );
    }

    @Test
    void createUpdateStatusSuccessfullyUpdatesStatus(){
        Association association = testDataManager.fetchAssociation("MiAssociation001").getFirst();
        StatusEnum status = StatusEnum.UNAUTHORISED;

        PrivateAccountsAssociationUpdateStatusPatch patchRequest = Mockito.mock(PrivateAccountsAssociationUpdateStatusPatch.class);
        Mockito.doReturn( patchRequest ).when( accountsAssociationEndpoint ).buildUpdateStatusRequest("theId123", association.getId(), status );

        String result = associationService.buildUpdateStatusRequest("theId123", association.getId(), status).get();

        Assertions.assertEquals(association.getId(), result);
        Mockito.verify(accountsAssociationEndpoint).buildUpdateStatusRequest("theId123", association.getId(), status);
    }

    @Test
    void createUpdateStatusThrowsNotFoundWhenAssociationDoesNotExist() throws ApiErrorResponseException, URIValidationException {
        final String associationId = "NFAssociation001";
        final StatusEnum status = StatusEnum.UNAUTHORISED;
        final PrivateAccountsAssociationUpdateStatusPatch mockRequest = Mockito.mock( PrivateAccountsAssociationUpdateStatusPatch.class );
        final ApiErrorResponseException.Builder builder = new ApiErrorResponseException.Builder( 404, "Not Found", new HttpHeaders() );

        Mockito.when( mockRequest.execute() ).thenThrow( new ApiErrorResponseException( builder ) );
        Mockito.when( accountsAssociationEndpoint.buildUpdateStatusRequest( "theId123", associationId, status ) ).thenReturn( mockRequest );

        Assertions.assertThrows(NotFoundRuntimeException.class, () -> associationService.buildUpdateStatusRequest( "theId123", associationId, status ).get() );
        Mockito.verify( accountsAssociationEndpoint ).buildUpdateStatusRequest( "theId123", associationId, status );
    }

    @Test
    void createUpdateStatusThrowsInternalServerErrorOnUnexpectedApiError() throws ApiErrorResponseException, URIValidationException {
        final String associationId = "MKAssociation001";
        final StatusEnum status = StatusEnum.UNAUTHORISED;
        final PrivateAccountsAssociationUpdateStatusPatch mockPrivateAccountsAssociationUpdateStatusPatch = Mockito.mock(PrivateAccountsAssociationUpdateStatusPatch.class);
        when( accountsAssociationEndpoint.buildUpdateStatusRequest( "theId123", associationId, status ) ).thenReturn( mockPrivateAccountsAssociationUpdateStatusPatch );
        ApiErrorResponseException apiException = new ApiErrorResponseException( new ApiErrorResponseException.Builder( 500, "Error", new HttpHeaders() ) );

        Mockito.doThrow( apiException ).when( mockPrivateAccountsAssociationUpdateStatusPatch ).execute();
        Assertions.assertThrows( InternalServerErrorRuntimeException.class, () -> associationService.buildUpdateStatusRequest( "theId123", associationId, status ).get() );
    }

    @Test
    void createUpdateStatusThrowsInternalServerErrorOnUnexpectedException() throws ApiErrorResponseException, URIValidationException {
        final var associationId = "MKAssociation001";
        final var status = StatusEnum.UNAUTHORISED;
        final PrivateAccountsAssociationUpdateStatusPatch mockPrivateAccountsAssociationUpdateStatusPatch = Mockito.mock( PrivateAccountsAssociationUpdateStatusPatch.class );
        when( accountsAssociationEndpoint.buildUpdateStatusRequest( "theId123", associationId, status ) ).thenReturn(mockPrivateAccountsAssociationUpdateStatusPatch);

        Mockito.doThrow( new NullPointerException( "Unexpected null" ) ).when( mockPrivateAccountsAssociationUpdateStatusPatch ).execute();
        Assertions.assertThrows( InternalServerErrorRuntimeException.class, () -> associationService.buildUpdateStatusRequest( "theId123", associationId, status ).get());
    }

}