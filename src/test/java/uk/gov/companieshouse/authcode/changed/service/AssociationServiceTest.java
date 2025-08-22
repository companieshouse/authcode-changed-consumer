package uk.gov.companieshouse.authcode.changed.service;

import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpResponseException.Builder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.associations.model.RequestBodyPut.StatusEnum;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.accountsassociation.request.PrivateAccountsAssociationForCompanyGet;
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

    @InjectMocks
    AssociationService associationService;

    private Mockers mockers;

    @BeforeEach
    void setup() {
        mockers = new Mockers(accountsAssociationEndpoint);
    }

    @Test
    void fetchAssociationDetailsWithNullInputReturnsInternalServerError()
            throws ApiErrorResponseException, URIValidationException {
        Mockito.doReturn(privateAccountsAssociationForCompanyGet)
                .when(accountsAssociationEndpoint)
                .buildGetAssociationsForCompanyRequest("MKUser001", false, 0, 1);
        Mockito.doThrow(NullPointerException.class)
                .when(privateAccountsAssociationForCompanyGet)
                .execute();
        Assertions.assertThrows(InternalServerErrorRuntimeException.class,
                () -> associationService.fetchAssociationDetails(null));
    }

    @Test
    void fetchUserDetailsWithMalformedInputReturnsInternalServerError() throws ApiErrorResponseException, URIValidationException {
        Mockito.doReturn(privateAccountsAssociationForCompanyGet)
                .when(accountsAssociationEndpoint)
                .buildGetAssociationsForCompanyRequest("MKUser001", false, 0, 1);
        Mockito.doThrow(new URIValidationException("Uri incorrectly formatted"))
                .when(privateAccountsAssociationForCompanyGet)
                .execute();
        Assertions.assertThrows(InternalServerErrorRuntimeException.class,
                () -> associationService.fetchAssociationDetails("$$$"));
    }

    @Test
    void fetchUserDetailsWithNonexistentUserReturnsNotFound() throws ApiErrorResponseException, URIValidationException {
        mockers.mockGetAssociationDetailsNotFound("111");
        Assertions.assertThrows(NotFoundRuntimeException.class, () -> associationService.fetchAssociationDetails("111"));
    }

    @Test
    void fetchUserDetailsReturnsInternalServerErrorWhenItReceivesApiErrorResponseWithNon404StatusCode()
            throws ApiErrorResponseException, URIValidationException {
        Mockito.doReturn(privateAccountsAssociationForCompanyGet)
                .when(accountsAssociationEndpoint)
                .buildGetAssociationsForCompanyRequest("MKUser001", false, 0, 1);
        Mockito.doThrow(new ApiErrorResponseException(new Builder(500, "Something unexpected happened", new HttpHeaders())))
                .when(privateAccountsAssociationForCompanyGet)
                .execute();
        Assertions.assertThrows(InternalServerErrorRuntimeException.class,
                () -> associationService.fetchAssociationDetails("111"));
    }

    @Test
    void fetchUserDetailsSuccessfullyFetchesUserData() throws ApiErrorResponseException, URIValidationException {
        mockers.mockGetAssociationDetails("333");
        Assertions.assertEquals("333", associationService.fetchAssociationDetails("333")
                .execute()
                .getData()
                .getItems()
                .getFirst()
                .getCompanyNumber());
    }

    @Test
    void createFetchUserDetailsRequestWithNullInputReturnsInternalServerError()
            throws ApiErrorResponseException, URIValidationException {
        Mockito.doReturn(privateAccountsAssociationForCompanyGet)
                .when(accountsAssociationEndpoint)
                .buildGetAssociationsForCompanyRequest("MKUser001", false, 0, 1);
        Mockito.doThrow(NullPointerException.class)
                .when(privateAccountsAssociationForCompanyGet)
                .execute();

        Assertions.assertThrows(InternalServerErrorRuntimeException.class, () ->
                associationService.fetchAssociationDetails(null)
        );
    }

    @Test
    void createFetchUserDetailsRequestWithMalformedInputReturnsInternalServerError()
            throws ApiErrorResponseException, URIValidationException {
        Mockito.doReturn(privateAccountsAssociationForCompanyGet)
                .when(accountsAssociationEndpoint)
                .buildGetAssociationsForCompanyRequest("", false, 0, 1);
        Mockito.doThrow(new URIValidationException("Uri incorrectly formatted"))
                .when(privateAccountsAssociationForCompanyGet)
                .execute();
        Assertions.assertThrows(InternalServerErrorRuntimeException.class, () -> associationService.fetchAssociationDetails("$"));
    }

    @Test
    void createFetchUserDetailsRequestWithNonexistentUserReturnsNotFound()
            throws ApiErrorResponseException, URIValidationException {
        mockers.mockGetAssociationDetailsNotFound("111");
        Assertions.assertThrows(NotFoundRuntimeException.class, () -> associationService.fetchAssociationDetails("111"));
    }

    @Test
    void createFetchUserDetailsRequestReturnsInternalServerErrorWhenItReceivesApiErrorResponseWithNon404StatusCode()
            throws ApiErrorResponseException, URIValidationException {
        Mockito.doReturn(privateAccountsAssociationForCompanyGet)
                .when(accountsAssociationEndpoint)
                .buildGetAssociationsForCompanyRequest("111", false, 0, 1);
        Mockito.doThrow(new ApiErrorResponseException(new Builder(500, "Something unexpected happened", new HttpHeaders())))
                .when(privateAccountsAssociationForCompanyGet)
                .execute();
        final var fetchUserDetailsRequest = associationService.fetchAssociationDetails("111");
        Assertions.assertThrows(ApiErrorResponseException.class, fetchUserDetailsRequest::execute);
    }

    @Test
    void createUpdateStatusSuccessfullyUpdatesStatus() throws ApiErrorResponseException, URIValidationException {
        final var associationId = "MKAssociation001";
        final StatusEnum status = StatusEnum.UNAUTHORISED;
        mockers.mockUpdateStatusSuccess(associationId, status);
        associationService.createUpdateStatusRequest(associationId, status);
        Mockito.verify(accountsAssociationEndpoint)
                .createUpdateStatusRequest(associationId, status);
    }

    @Test
    void createUpdateStatusThrowsNotFoundWhenAssociationDoesNotExist() throws ApiErrorResponseException, URIValidationException {
        mockers.mockUpdateStatusNotFound("MKAssociation001", StatusEnum.UNAUTHORISED);
        Assertions.assertThrows(NotFoundRuntimeException.class,
                () -> associationService.createUpdateStatusRequest("MKAssociation001", StatusEnum.UNAUTHORISED));
    }


    @Test
    void createUpdateStatusThrowsInternalServerErrorOnUnexpectedApiError()
            throws ApiErrorResponseException, URIValidationException {
        mockers.mockUpdateStatusApiError("MKAssociation001", StatusEnum.UNAUTHORISED, 500);
        Assertions.assertThrows(InternalServerErrorRuntimeException.class,
                () -> associationService.createUpdateStatusRequest("MKAssociation001", StatusEnum.UNAUTHORISED));
    }

    @Test
    void createUpdateStatusThrowsInternalServerErrorOnUnexpectedException()
            throws ApiErrorResponseException, URIValidationException {
        Mockito.doThrow(new NullPointerException("Unexpected null"))
                .when(accountsAssociationEndpoint)
                .createUpdateStatusRequest("MKAssociation001", StatusEnum.UNAUTHORISED);
        Assertions.assertThrows(InternalServerErrorRuntimeException.class,
                () -> associationService.createUpdateStatusRequest("MKAssociation001", StatusEnum.UNAUTHORISED));
    }
}