package uk.gov.companieshouse.authcode.changed.rest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.handler.accountsassociation.PrivateAccountsAssociationResourceHandler;
import uk.gov.companieshouse.api.handler.accountsassociation.request.PrivateAccountsAssociationForCompanyGet;
import uk.gov.companieshouse.api.handler.accountsassociation.request.PrivateAccountsAssociationUpdateStatusPatch;
import uk.gov.companieshouse.api.handler.accountsuser.PrivateAccountsUserResourceHandler;
import uk.gov.companieshouse.api.handler.accountsuser.request.PrivateAccountsUserUserGet;
import uk.gov.companieshouse.authcode.changed.utils.ApiClientUtil;

@ExtendWith(MockitoExtension.class)
@Tag("unit-test")
public class AccountsAssociationEndpointTest {

    @Mock
    private ApiClientUtil apiClientService;

    @Mock
    private InternalApiClient internalApiClient;

    @Mock
    private PrivateAccountsUserResourceHandler privateAccountsUserResourceHandler;

    @Mock
    private PrivateAccountsAssociationResourceHandler privateAccountsAssociationResourceHandler;

    @Mock
    private PrivateAccountsAssociationUpdateStatusPatch privateAccountsAssociationUpdateStatusPatch;

    @Mock
    private PrivateAccountsAssociationForCompanyGet privateAccountsAssociationForCompanyGet;

    @Mock
    private PrivateAccountsUserUserGet privateAccountsUserUserGet;

    @InjectMocks
    private AccountsAssociationEndpoint accountsAssociationEndpoint;

    @Test
    void GetAssociationsForCompanyRequestWithNullInputThrowsNullPointerException() {
        Assertions.assertThrows( NullPointerException.class, () -> accountsAssociationEndpoint.createGetAssociationsForCompanyRequest( null, false, 1, 0 ) );
    }

    @Test
    void getAssociationsForCompanyWithMalformedInputReturnsBadRequest() {
        Assertions.assertThrows( NullPointerException.class, () -> accountsAssociationEndpoint.createGetAssociationsForCompanyRequest( "$$$$", false, 1, 0 ) );
    }

//    @Test
//    void getAssociationsForCompanyWithValidInputReturnsRequest() {
//        Mockito.doReturn( internalApiClient ).when( apiClientService ).getInternalApiClient( Mockito.anyString() );
//        Mockito.doReturn( privateAccountsAssociationResourceHandler ).when( internalApiClient ).privateAccountsAssociationResourceHandler();
//        Mockito.doReturn( privateAccountsAssociationForCompanyGet ).when( privateAccountsAssociationResourceHandler ).getAssociationsForCompany( Mockito.anyString(), Mockito.anyBoolean(), Mockito.anyInt(), Mockito.anyInt() );
//        AssociationsList associationsList = new AssociationsList();
//
//        ApiResponse<AssociationsList> apiResponse = new ApiResponse<>(200, Map.of(), associationsList);
//        Mockito.doReturn(apiResponse)
//                .when(privateAccountsAssociationForCompanyGet);
//
//        PrivateAccountsAssociationForCompanyGet result = accountsAssociationEndpoint.createGetAssociationsForCompanyRequest("12345678", false, 1, 0);
//        ApiResponse<AssociationsList> response = result.execute();
//
//        Assertions.assertEquals(200, response.getStatusCode());
//        Assertions.assertNotNull(response.getData());
//    }
//
//    @Test
//    void getAssociationsForCompanyWithNonexistentCompanyReturnsNotFound() {
//        Mockito.doReturn(internalApiClient).when(apiClientService).getInternalApiClient(Mockito.anyString());
//        Mockito.doReturn(privateAccountsAssociationResourceHandler).when(internalApiClient).privateAccountsAssociationResourceHandler();
//        Mockito.doReturn(privateAccountsAssociationForCompanyGet).when(privateAccountsAssociationResourceHandler)
//                .getAssociationsForCompany(Mockito.anyString(), Mockito.anyBoolean(), Mockito.anyInt(), Mockito.anyInt());
//
//        ApiResponse<AssociationsList> apiResponse = new ApiResponse<>(404, Map.of(), null);
//        Mockito.doReturn(apiResponse)
//                .when(privateAccountsAssociationForCompanyGet);
//
//        Assertions.assertThrows( NotFoundRuntimeException.class, () -> accountsAssociationEndpoint.createGetAssociationsForCompanyRequest("12345678", false, 1, 0));
//    }

    @Test
    void createUpdateStatusRequestWithNullInputThrowsNullPointerException() {
        Assertions.assertThrows( NullPointerException.class, () -> accountsAssociationEndpoint.createUpdateStatusRequest( null, null ) );
    }

    @Test
    void createUpdateStatusRequestWithMalformedInputReturnsBadRequest() {
        Assertions.assertThrows( NullPointerException.class, () -> accountsAssociationEndpoint.createUpdateStatusRequest( "$$$$", null ) );
    }

    //

//    @Test
//    void createUpdateStatusRequestWithValidInputReturnsRequest() {
//        Mockito.doReturn( internalApiClient ).when( apiClientService ).getInternalApiClient( Mockito.anyString() );
//        Mockito.doReturn( privateAccountsAssociationResourceHandler ).when( internalApiClient ).privateAccountsAssociationResourceHandler();
//        Mockito.doReturn( privateAccountsAssociationUpdateStatusPatch ).when( privateAccountsAssociationResourceHandler ).updateAssociationStatusForId( Mockito.anyString(), Mockito.any() );
//
//        PrivateAccountsAssociationUpdateStatusPatch result = accountsAssociationEndpoint.createUpdateStatusRequest("12345678", StatusEnum.UNAUTHORISED);
//        Assertions.assertNotNull(result);
//    }

}
