package uk.gov.companieshouse.authcode.changed.service;

import static uk.gov.companieshouse.authcode.changed.utils.RequestContextUtil.getXRequestId;
import uk.gov.companieshouse.api.accounts.associations.model.AssociationsList;
import uk.gov.companieshouse.api.accounts.associations.model.RequestBodyPut.StatusEnum;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.accountsassociation.request.PrivateAccountsAssociationForCompanyGet;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.authcode.changed.exceptions.InternalServerErrorRuntimeException;
import uk.gov.companieshouse.authcode.changed.exceptions.NotFoundRuntimeException;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.authcode.changed.rest.AccountsAssociationEndpoint;
import uk.gov.companieshouse.authcode.changed.utils.StaticPropertyUtil;

@Service
public class AssociationService {

    public final AccountsAssociationEndpoint accountsAssociationEndpoint;

    private static final Logger LOG = LoggerFactory.getLogger(StaticPropertyUtil.APPLICATION_NAMESPACE);

    public AssociationService(AccountsAssociationEndpoint accountsAssociationEndpoint) {
        this.accountsAssociationEndpoint = accountsAssociationEndpoint;
    }

    public void setCompanyAssociationToUnauthorised(String companyNumber) throws ApiErrorResponseException, URIValidationException {
        LOG.info("Setting association status to UNAUTHORISED for company number: " + companyNumber);
        PrivateAccountsAssociationForCompanyGet associationsList = fetchAssociationDetails(companyNumber);
        if (associationsList != null ) {
            associationsList.execute().getData().getItems().forEach(association -> {
                String associationId = association.getId();
                LOG.info("Updating status for association ID: " + associationId);
                createUpdateStatusRequest(associationId, StatusEnum.UNAUTHORISED);
            });
        } else {
            LOG.error("No associations found for company number: " + companyNumber);
        }
    }

    public PrivateAccountsAssociationForCompanyGet fetchAssociationDetails( String companyNumber ){
        LOG.info("ROSEY");
        final var XRequestId =  getXRequestId();
        try {
            LOG.debugContext( XRequestId, String.format( "Sending request to account-association-api: GET /associations/companies/{company_number}. Attempting to fetch Association details for company %s ", companyNumber ), null );
            return accountsAssociationEndpoint.buildGetAssociationsForCompanyRequest( companyNumber, false, 0, 1 );
        } catch (Exception exception ) {
            LOG.errorContext(  XRequestId, new Exception( String.format( "Failed to retrieve association details for company number: %s: " , companyNumber ) ), null );
            throw new InternalServerErrorRuntimeException( "Failed to retrieve user detail ", exception);
        }
    }


    public void createUpdateStatusRequest( String associationId, StatusEnum statusEnum ) {
        final var XRequestId =  getXRequestId();
        try {
            LOG.debugContext( XRequestId, String.format( "Updating status for association ID %s with status %s", associationId, statusEnum ), null );
            accountsAssociationEndpoint.createUpdateStatusRequest( associationId, statusEnum );
        } catch (ApiErrorResponseException exception) {
            if (exception.getStatusCode() == 404) {
                LOG.errorContext(XRequestId, new Exception( String.format( "Association not found for ID: %s " , associationId ), exception ), null );
                throw new NotFoundRuntimeException( String.format( "Association not found for ID: %s " , associationId), exception);
            } else {
                LOG.errorContext( XRequestId, new Exception( String.format( "Failed to update status for association ID: %s " , associationId ), exception ), null);
                throw new InternalServerErrorRuntimeException( String.format( "Failed to update status for association ID: %s" , associationId ), exception );
            }
        } catch (Exception exception) {
            LOG.error( XRequestId, new Exception( String.format( "Failed to update status for association ID: %s" , associationId ) , exception), null );
            throw new InternalServerErrorRuntimeException( String.format( "Unexpected error while updating status for association ID: %s" , associationId ), exception );
        }
    }


}
