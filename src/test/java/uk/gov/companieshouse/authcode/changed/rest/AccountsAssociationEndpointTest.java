package uk.gov.companieshouse.authcode.changed.rest;

import static org.mockito.ArgumentMatchers.any;

import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpResponseException;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.accounts.associations.model.Association;
import uk.gov.companieshouse.api.accounts.associations.model.AssociationsList;
import uk.gov.companieshouse.api.accounts.associations.model.RequestBodyPut.StatusEnum;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.accountsassociation.PrivateAccountsAssociationResourceHandler;
import uk.gov.companieshouse.api.handler.accountsassociation.request.PrivateAccountsAssociationForCompanyGet;
import uk.gov.companieshouse.api.handler.accountsassociation.request.PrivateAccountsAssociationUpdateStatusPatch;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.authcode.changed.service.ApiClientService;

@ExtendWith(MockitoExtension.class)
@Tag("unit-test")
public class AccountsAssociationEndpointTest {

    @Mock
    private ApiClientService apiClientService;

    @Mock
    private InternalApiClient internalApiClient;

    @Mock
    private PrivateAccountsAssociationResourceHandler privateAccountsAssociationResourceHandler;

    @Mock
    private PrivateAccountsAssociationUpdateStatusPatch privateAccountsAssociationUpdateStatusPatch;

    @Mock
    private PrivateAccountsAssociationForCompanyGet privateAccountsAssociationForCompanyGet;

    @InjectMocks
    private AccountsAssociationEndpoint accountsAssociationEndpoint;

    @Test
    void getAssociationsForCompanyRequestWithNullInputThrowsNullPointerException() {
        Assertions.assertThrows(NullPointerException.class, () -> accountsAssociationEndpoint.buildGetAssociationsForCompanyRequest(null, false, 1, 0) );
    }

    @Test
    void getAssociationsForCompanyWithMalformedInputReturnsURIValidationException() throws ApiErrorResponseException, URIValidationException {
        Mockito.doReturn( internalApiClient ).when(apiClientService).getInternalApiClient( any() );
        Mockito.doReturn( privateAccountsAssociationResourceHandler ).when( internalApiClient ).privateAccountsAssociationResourceHandler();
        Mockito.doReturn( privateAccountsAssociationForCompanyGet ).when( privateAccountsAssociationResourceHandler ).getAssociationsForCompany( any(), any(), any(), any() );

        Mockito.doThrow( new URIValidationException( "Malformed URI" ) ).when( privateAccountsAssociationForCompanyGet ).execute();

        Assertions.assertThrows( URIValidationException.class, () -> accountsAssociationEndpoint.buildGetAssociationsForCompanyRequest( "$$$$", false, 1, 0 ).execute() );
    }

    @Test
    void getAssociationsForCompanyWithNonexistentCompanyReturnsNotFound() throws ApiErrorResponseException, URIValidationException {
        Mockito.doReturn( internalApiClient ).when(apiClientService).getInternalApiClient( any() );
        Mockito.doReturn( privateAccountsAssociationResourceHandler ).when( internalApiClient ).privateAccountsAssociationResourceHandler();
        Mockito.doReturn( privateAccountsAssociationForCompanyGet ).when( privateAccountsAssociationResourceHandler ).getAssociationsForCompany( any(), any(), any(), any() );

        Mockito.doThrow( new ApiErrorResponseException( new HttpResponseException.Builder( 404, "Not Found", new HttpHeaders() ) ) ).when( privateAccountsAssociationForCompanyGet ).execute();
        Assertions.assertThrows( ApiErrorResponseException.class, () -> accountsAssociationEndpoint.buildGetAssociationsForCompanyRequest( "MKCOMP001", false, 0, 3 ).execute() );
    }

    @Test
    void getAssociationsForCompanyWithValidInputReturnsRequest() throws ApiErrorResponseException, URIValidationException {
        Mockito.doReturn(internalApiClient).when(apiClientService).getInternalApiClient(any());
        Mockito.doReturn(privateAccountsAssociationResourceHandler).when(internalApiClient).privateAccountsAssociationResourceHandler();
        Mockito.doReturn(privateAccountsAssociationForCompanyGet).when(privateAccountsAssociationResourceHandler).getAssociationsForCompany(any(), any(), any(), any());

        final var AssociationsList = new AssociationsList().items(List.of(new Association().companyNumber("MKCOMP001")));
        final var intendedResponse = new ApiResponse<>(200, Map.of(), AssociationsList);

        Mockito.doReturn(intendedResponse).when(privateAccountsAssociationForCompanyGet).execute();
        final var response = accountsAssociationEndpoint.buildGetAssociationsForCompanyRequest("MKCOMP001", false, 0, 3).execute();

        Assertions.assertEquals(200, response.getStatusCode());
        Assertions.assertEquals("MKCOMP001", response.getData().getItems().getFirst().getCompanyNumber());
    }

    @Test
    void updateStatusRequestWithNonExistentAssociationThrowsNotFound() throws ApiErrorResponseException, URIValidationException {
        Mockito.doReturn(internalApiClient).when(apiClientService).getInternalApiClient(Mockito.any());
        Mockito.doReturn(privateAccountsAssociationResourceHandler).when(internalApiClient).privateAccountsAssociationResourceHandler();
        Mockito.doReturn(privateAccountsAssociationUpdateStatusPatch).when(privateAccountsAssociationResourceHandler).updateAssociationStatusForId(Mockito.any(), Mockito.any());

        final var notFound = new ApiErrorResponseException(new ApiErrorResponseException.Builder(404, "Not Found", new HttpHeaders()));
        Mockito.doThrow(notFound).when(privateAccountsAssociationUpdateStatusPatch).execute();
        final var request = accountsAssociationEndpoint.buildUpdateStatusRequest("12345678", StatusEnum.UNAUTHORISED);

        Assertions.assertThrows(ApiErrorResponseException.class, request::execute);
        Mockito.verify(privateAccountsAssociationUpdateStatusPatch).execute();
    }

    @Test
    void buildUpdateStatusRequestWithNullInputThrowsNullPointerException() {
        Assertions.assertThrows(NullPointerException.class, () -> accountsAssociationEndpoint.buildUpdateStatusRequest(null, null));
    }

    @Test
    void buildUpdateStatusRequestWithMalformedInputReturnsBadRequest() throws ApiErrorResponseException, URIValidationException {
        Mockito.doReturn( internalApiClient ).when(apiClientService).getInternalApiClient( Mockito.any() );
        Mockito.doReturn( privateAccountsAssociationResourceHandler ).when( internalApiClient ).privateAccountsAssociationResourceHandler();
        Mockito.doReturn( privateAccountsAssociationUpdateStatusPatch ).when( privateAccountsAssociationResourceHandler ).updateAssociationStatusForId( any(), any() );

        Mockito.doThrow( new URIValidationException( "Malformed URI" ) ).when( privateAccountsAssociationUpdateStatusPatch ).execute();

        Assertions.assertThrows(URIValidationException.class, () -> accountsAssociationEndpoint.buildUpdateStatusRequest("$$$$", null).execute());
    }

    @Test
    void buildUpdateStatusRequestWithValidInputReturnsRequest() throws ApiErrorResponseException, URIValidationException {
        Mockito.doReturn( internalApiClient ).when(apiClientService).getInternalApiClient( Mockito.any() );
        Mockito.doReturn( privateAccountsAssociationResourceHandler ).when( internalApiClient ).privateAccountsAssociationResourceHandler();
        Mockito.doReturn( privateAccountsAssociationUpdateStatusPatch ).when( privateAccountsAssociationResourceHandler ).updateAssociationStatusForId( Mockito.any(), Mockito.any() );

        Mockito.doReturn( new ApiResponse<>( 200, Map.of(), null ) ).when( privateAccountsAssociationUpdateStatusPatch ).execute();
        final var response = accountsAssociationEndpoint.buildUpdateStatusRequest( "MiAssociation001", StatusEnum.UNAUTHORISED ).execute();

        Assertions.assertEquals(200, response.getStatusCode());
    }

}
