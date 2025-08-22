package uk.gov.companieshouse.authcode.changed.common;

import com.google.api.client.http.HttpHeaders;
import java.util.List;
import java.util.Map;
import org.mockito.Mockito;
import uk.gov.companieshouse.api.accounts.associations.model.AssociationsList;
import uk.gov.companieshouse.api.accounts.associations.model.RequestBodyPut.StatusEnum;
import uk.gov.companieshouse.api.accounts.user.model.User;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.accountsassociation.request.PrivateAccountsAssociationForCompanyGet;
import uk.gov.companieshouse.api.handler.accountsassociation.request.PrivateAccountsAssociationUpdateStatusPatch;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.authcode.changed.rest.AccountsAssociationEndpoint;


public class Mockers {

    private static final TestDataManager testDataManager = TestDataManager.getInstance();

    private final AccountsAssociationEndpoint accountsAssociationEndpoint;


    public Mockers(final AccountsAssociationEndpoint accountsAssociationEndpoint) {
        this.accountsAssociationEndpoint = accountsAssociationEndpoint;
    }

    public void mockGetAssociationDetails(final AssociationsList associationsList) throws ApiErrorResponseException, URIValidationException {
        final var request = Mockito.mock(PrivateAccountsAssociationForCompanyGet.class);
        Mockito.doReturn( request ).when( accountsAssociationEndpoint ).buildGetAssociationsForCompanyRequest( associationsList.getItems().getFirst().getCompanyNumber(), false, 0, 1 );
        Mockito.lenient().doReturn( new ApiResponse<>( 200, Map.of(), associationsList ) ).when( request ).execute();
    }

    public void mockGetAssociationDetails(final String... companyNumbers)
            throws ApiErrorResponseException, URIValidationException {
        for (final String companyNumber : companyNumbers) {
            final var userDetails = new User().userId("MKUser003").email("peach@mushroom.kingdom");
            final var associationsList = new AssociationsList();
            associationsList.setItems(
                    List.of(testDataManager.createAssociationFromCompanyNumber(companyNumber, userDetails)));
            mockGetAssociationDetails( associationsList );
        }
    }

    public void mockUpdateStatusSuccess(final String associationId, final StatusEnum statusEnum) {
        final var patchRequest = Mockito.mock( PrivateAccountsAssociationUpdateStatusPatch.class );
        Mockito.doReturn( patchRequest ).when( accountsAssociationEndpoint ).createUpdateStatusRequest( associationId, statusEnum );
    }

    public void mockUpdateStatusNotFound(final String associationId, final StatusEnum statusEnum) throws ApiErrorResponseException, URIValidationException {
        var mockRequest = Mockito.mock( PrivateAccountsAssociationUpdateStatusPatch.class );
        ApiErrorResponseException.Builder builder = new ApiErrorResponseException.Builder( 404, "Not Found", new HttpHeaders() );
        Mockito.when( mockRequest.execute() ).thenThrow( new ApiErrorResponseException( builder ) );
        Mockito.when( accountsAssociationEndpoint.createUpdateStatusRequest( associationId, statusEnum ) ).thenReturn( mockRequest );
    }
}