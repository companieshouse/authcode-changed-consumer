package uk.gov.companieshouse.authcode.changed.common;

import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpResponseException.Builder;
import java.util.List;
import java.util.Map;
import org.mockito.Mockito;
import uk.gov.companieshouse.api.accounts.associations.model.AssociationsList;
import uk.gov.companieshouse.api.accounts.associations.model.RequestBodyPut.StatusEnum;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.accountsassociation.request.PrivateAccountsAssociationForCompanyGet;
import uk.gov.companieshouse.api.handler.accountsassociation.request.PrivateAccountsAssociationUpdateStatusPatch;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.authcode.changed.rest.AccountsAssociationEndpoint;
import uk.gov.companieshouse.authcode.changed.service.AssociationService;


public class Mockers {

    private static final TestDataManager testDataManager = TestDataManager.getInstance();

    private final AccountsAssociationEndpoint accountsAssociationEndpoint;


    public Mockers( final AccountsAssociationEndpoint accountsAssociationEndpoint) {
        this.accountsAssociationEndpoint = accountsAssociationEndpoint;
    }

    public void mockGetAssociationDetails( final AssociationsList associationsList ) throws ApiErrorResponseException, URIValidationException {
        final var request = Mockito.mock( PrivateAccountsAssociationForCompanyGet.class );
        Mockito.doReturn( request ).when(accountsAssociationEndpoint).buildGetAssociationsForCompanyRequest(
                associationsList.getItems().getFirst().getCompanyNumber(), false, 0, 1 );
        Mockito.lenient().doReturn( new ApiResponse<>( 200, Map.of(), associationsList ) ).when( request ).execute();
    }

    public void mockGetAssociationDetails( final String... companyNumbers ) throws ApiErrorResponseException, URIValidationException {
        for ( final String companyNumber: companyNumbers ){
            final var associationsList = new AssociationsList();
                    associationsList.setItems(
                    List.of(testDataManager.createAssociationFromCompanyNumber(companyNumber)));
            mockGetAssociationDetails( associationsList );
        }
    }

    public void mockGetAssociationDetailsNotFound(final String... companyNumbers)
            throws ApiErrorResponseException, URIValidationException {

        for (final String companyNumber : companyNumbers) {
            Mockito.doThrow(new ApiErrorResponseException(
                            new Builder(404, "Not Found", new HttpHeaders())))
                    .when(accountsAssociationEndpoint)
                    .buildGetAssociationsForCompanyRequest(companyNumber, false, 0, 1);
        }
    }

    public void mockUpdateStatusSuccess(final String associationId, final StatusEnum statusEnum)
            throws ApiErrorResponseException, URIValidationException {
        final var patchRequest = Mockito.mock(PrivateAccountsAssociationUpdateStatusPatch.class);
        Mockito.doReturn(patchRequest)
                .when(accountsAssociationEndpoint)
                .createUpdateStatusRequest(associationId, statusEnum);
    }

    public void mockUpdateStatusNotFound(final String associationId, final StatusEnum statusEnum)
            throws ApiErrorResponseException, URIValidationException {
        Mockito.doThrow(new ApiErrorResponseException(
                        new Builder(404, "Not Found", new HttpHeaders())))
                .when(accountsAssociationEndpoint)
                .createUpdateStatusRequest(associationId, statusEnum);
    }

    public void mockUpdateStatusApiError(final String associationId, final StatusEnum statusEnum, int statusCode)
            throws ApiErrorResponseException, URIValidationException {
        Mockito.doThrow(new ApiErrorResponseException(
                        new Builder(statusCode, "Error", new HttpHeaders())))
                .when(accountsAssociationEndpoint)
                .createUpdateStatusRequest(associationId, statusEnum);
    }


}