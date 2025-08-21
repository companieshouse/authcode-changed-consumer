//package uk.gov.companieshouse.authcode.changed.service;
//
//import static org.junit.Assert.assertThrows;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyBoolean;
//import static org.mockito.ArgumentMatchers.anyInt;
//import static org.mockito.ArgumentMatchers.isNull;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import com.google.api.client.http.HttpHeaders;
//import com.google.api.client.http.HttpResponseException.Builder;
//import java.util.List;
//import java.util.Map;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Tag;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.internal.verification.Times;
//import org.mockito.junit.jupiter.MockitoExtension;
//import uk.gov.companieshouse.api.InternalApiClient;
//import uk.gov.companieshouse.api.accounts.associations.model.AssociationsList;
//import uk.gov.companieshouse.api.accounts.associations.model.Links;
//import uk.gov.companieshouse.api.accounts.user.model.User;
//import uk.gov.companieshouse.api.error.ApiErrorResponseException;
//import uk.gov.companieshouse.api.handler.accountsassociation.PrivateAccountsAssociationResourceHandler;
//import uk.gov.companieshouse.api.handler.accountsassociation.request.PrivateAccountsAssociationForCompanyGet;
//import uk.gov.companieshouse.api.handler.accountsassociation.request.PrivateAccountsAssociationUpdateStatusPatch;
//import uk.gov.companieshouse.api.handler.exception.URIValidationException;
//import uk.gov.companieshouse.api.model.ApiResponse;
//import uk.gov.companieshouse.authcode.changed.common.TestDataManager;
//import uk.gov.companieshouse.authcode.changed.exceptions.InternalServerErrorRuntimeException;
//import uk.gov.companieshouse.authcode.changed.rest.AccountsAssociationEndpoint;
//import uk.gov.companieshouse.authcode.changed.utils.ApiClientUtil;
//
//@ExtendWith(MockitoExtension.class)
//@Tag("unit-test")
//class AssociationServiceTest {
//
//    @Mock
//    AccountsAssociationEndpoint accountsAssociationEndpoint;
//
////    @Mock
//    AssociationService associationService = new AssociationService(accountsAssociationEndpoint);
//
//    private static final TestDataManager testDataManager = TestDataManager.getInstance();
//
//    @Test
//    void
//
//
//
//
//
//
////
////    @Test
////    void fetchAssociationDetailsForCompanyWithValidInputReturnsAssociationsList()
////            throws ApiErrorResponseException, URIValidationException {
////        Mockito.doReturn(mock(PrivateAccountsAssociationForCompanyGet.class))
////                .when(accountsAssociationEndpoint)
////                .buildGetAssociationsForCompanyRequest("MKCOMP001", false, 0, 3);
////        final var associationsList = new AssociationsList()
////                .items(List.of(testDataManager.getAssociation1(), testDataManager.getAssociation2(),
////                        testDataManager.getAssociation3()));
////        final var response = accountsAssociationEndpoint.buildGetAssociationsForCompanyRequest(
////                "MKCOMP001", false, 0, 3).execute();
////        final var intendedResponse = new ApiResponse<>(200, Map.of(), associationsList);
////
////        Mockito.doReturn(intendedResponse)
////                .when(accountsAssociationEndpoint)
////                .buildGetAssociationsForCompanyRequest("MKCOMP001", false, 0, 3)
////                .execute();
////
////        Assertions.assertEquals(Map.of("MKCOMP011", "Mushroom Kingdom"),
////                response.getData().getItems().getFirst().getCompanyName());
////        ;
////    }
////
////    @Test
////    void fetchAssociationDetailsForCompanyWithValidInputReturnsAssociationsList2()
////            throws ApiErrorResponseException, URIValidationException {
////        final var request = Mockito.mock(PrivateAccountsAssociationForCompanyGet.class);
////
////        //apiClientUtil.getInternalApiClient(accountApiUrl)
////
////        Mockito.doReturn(request).when(accountsAssociationEndpoint)
////                .buildGetAssociationsForCompanyRequest("MKCOMP001", false, 0, 3);
////        final var associationsList = new AssociationsList().items(
////                List.of(
////                        testDataManager.getAssociation1(),
////                        testDataManager.getAssociation2(),
////                        testDataManager.getAssociation3()
////                )
////        );
////        final var intendedResponse = new ApiResponse<>(200, Map.of(), associationsList);
////        Mockito.doReturn(intendedResponse).when(request).execute();
////        final var result = associationService.fetchAssociationDetails("MKCOMP001").execute().getData();
////        Assertions.assertEquals(3, result.getItems().size());
////        Assertions.assertEquals(
////                "Mushroom Kingdom", result.getItems().getFirst().getCompanyName()
////        );
////        Assertions.assertEquals(
////                "MKCOMP001", result.getItems().getFirst().getCompanyNumber()
////        );
////
////        Mockito.verify(accountsAssociationEndpoint)
////                .buildGetAssociationsForCompanyRequest("MKCOMP001", false, 0, 3);
////        Mockito.verify(request).execute();
////    }
////
////
//
//
////    @Test
////    void getAssociationDetailsForCompanyWithNullInputReturnsInternalServerError() throws ApiErrorResponseException, URIValidationException {
////        PrivateAccountsAssociationForCompanyGet request = mock(PrivateAccountsAssociationForCompanyGet.class);
////        when(accountsAssociationEndpoint.buildGetAssociationsForCompanyRequest(isNull(), anyBoolean(), anyInt(), anyInt())).thenReturn(request);
////        when(request.execute()).thenThrow(new NullPointerException("Company number cannot be null"));
////
////        assertThrows(InternalServerErrorRuntimeException.class, () -> associationService.fetchAssociationDetails(null));
////    }
//
////    @Test
////    void getAssociationDetailsForCompanyWithNullInputReturnsInternalServerError() throws ApiErrorResponseException, URIValidationException {
////        when( accountsAssociationEndpoint.createGetAssociationsForCompanyRequest( isNull(), anyBoolean(), anyInt(), anyInt() ) ) .thenThrow(new NullPointerException("Company number cannot be null"));
////        assertThrows(InternalServerErrorRuntimeException.class, () -> associationService.fetchAssociationDetails(null));
////    }
//
////    @Test
////    void getAssociationDetailsForCompanyWithNullInputReturnsInternalServerError() throws ApiErrorResponseException, URIValidationException {
////        Mockito.doReturn( privateAccountsAssociationForCompanyGet ).when( accountsAssociationEndpoint ).createGetAssociationsForCompanyRequest( Mockito.isNull(), Mockito.anyBoolean(), Mockito.anyInt(), Mockito.anyInt() );
////        Mockito.doThrow( new NullPointerException("Company number cannot be null") ).when( privateAccountsAssociationForCompanyGet ).execute();
////        Assertions.assertThrows( InternalServerErrorRuntimeException.class , () -> associationService.fetchAssociationDetails( null ) );
////    }
//
////    @Test
////    void getAssociationDetailsForCompanyWithMalformedInputReturnsInternalServerError() throws ApiErrorResponseException, URIValidationException {
////        Mockito.doReturn( privateAccountsAssociationForCompanyGet ).when( accountsAssociationEndpoint ).buildGetAssociationsForCompanyRequest( isNull(), anyBoolean(), anyInt(), anyInt() );
////        Mockito.doThrow( new URIValidationException("Uri incorrectly formated") ) .when( privateAccountsAssociationForCompanyGet ).execute();
////        Assertions.assertThrows( InternalServerErrorRuntimeException.class , () -> associationService.fetchAssociationDetails( "$$$" ) );
////    }
//
////// mock details that come back from association api.
////// also need to mockito verify to see what we're sending to associaiton api is true.
////// verify we sent the right variables
////
////
////// let's say in our mock we have 3 associations.
////// we need to check that our patch gets sent 3 times.
//
////    @Test
////    void getAssociationDetailsForCompanyWithValidInputReturnsAssociationsList() throws ApiErrorResponseException, URIValidationException {
////        Mockito.doReturn( internalApiClient).when( apiClientService).getInternalApiClient( any() );
////        Mockito.doReturn( privateAccountsAssociationResourceHandler ).when( internalApiClient ).privateAccountsAssociationResourceHandler();
////        Mockito.doReturn( privateAccountsAssociationForCompanyGet ).when( privateAccountsAssociationResourceHandler ) .getAssociationsForCompany( any(), any(), any(), any() );
////        Mockito.doThrow( new ApiErrorResponseException( new Builder(400, "Not Found", new HttpHeaders() ) ) ) .when( privateAccountsAssociationForCompanyGet ).execute();
////
////        final var intendedResponse = new ApiResponse<>( 200, Map.of(), new AssociationsList(). items(List.of(testDataManager.getAssociation1(), testDataManager.getAssociation2(), testDataManager.getAssociation3())));
////        Mockito.doReturn(intendedResponse).when(privateAccountsAssociationForCompanyGet).execute();
////        final var response = accountsAssociationEndpoint.buildGetAssociationsForCompanyRequest("MKCOMP001", false, 0, 3);
////
////        Assertions.assertEquals(200, response.execute().getStatusCode());
////        //Assertions.assertEquals("MKCOMP001", response.getData().getItems().get(0).getCompanyNumber());
////    }
//////
////    @Test
////    void testGetAssociationDetailsReturnsAssociationList() throws ApiErrorResponseException, URIValidationException {
////        final var User = testDataManager.getAssociation1().setCompanyNumber("MKCOMP001");
////        final var associationsList = testDataManager.createAssociation( testDataManager.getAssociation1().getId(),
////                testDataManager.getAssociation1().getUserId(),
////                testDataManager.getAssociation1().companyNumber(User),
////                testDataManager.getAssociation1().getStatus() );
////
////        Mockito.doReturn(new ApiResponse<>(200, null, associationsList))
////                .when(accountsAssociationEndpoint).createGetAssociationsForCompanyRequest(Mockito.anyString(), Mockito.anyBoolean(), Mockito.anyInt(), Mockito.anyInt());
////
////        AssociationsList result = associationService.fetchAssociationDetails("MKCOMP001");
////        Assertions.assertNotNull(result);
////        Assertions.assertEquals(1, result.getItems().size());
////       }
//
//}
