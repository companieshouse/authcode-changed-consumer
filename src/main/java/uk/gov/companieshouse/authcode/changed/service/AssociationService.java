package uk.gov.companieshouse.authcode.changed.service;

import java.util.function.Supplier;
import uk.gov.companieshouse.api.accounts.associations.model.AssociationsList;
import uk.gov.companieshouse.api.accounts.associations.model.RequestBodyPut.StatusEnum;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.authcode.changed.exceptions.InternalServerErrorRuntimeException;
import uk.gov.companieshouse.authcode.changed.exceptions.NotFoundRuntimeException;
import static uk.gov.companieshouse.authcode.changed.utils.LoggingUtil.LOGGER;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.authcode.changed.rest.AccountsAssociationEndpoint;

@Service
public class AssociationService {

    public final AccountsAssociationEndpoint accountsAssociationEndpoint;

    private AssociationService(final AccountsAssociationEndpoint accountsAssociationEndpoint) {
        this.accountsAssociationEndpoint = accountsAssociationEndpoint;
    }

    public Supplier<AssociationsList> buildFetchAssociationsForCompanyRequest(final String xRequestId, final String companyNumber, final boolean includeRemoved, final int pageIndex, final int itemsPerPage) {
        final var request = accountsAssociationEndpoint.buildGetAssociationsForCompanyRequest(xRequestId, companyNumber, includeRemoved, pageIndex, itemsPerPage);
        return () -> {
            try {
                LOGGER.debug(String.format("Sending request to account-association-api: GET /associations/companies/{company_number}. Attempting to fetch Association details for company %s ", companyNumber), null);
                return request.execute().getData();
            } catch (ApiErrorResponseException exception) {
                if (exception.getStatusCode() == 404) {
                    throw new NotFoundRuntimeException( String.format( "Association not found for Company Number: %s ", companyNumber ), new Exception( String.format( "Association not found for Company Number: %s ", companyNumber ) ) );
                } else {
                    throw new InternalServerErrorRuntimeException( String.format( "Failed to fetch for Company Number: %s ", companyNumber ), new Exception( String.format( "Failed to fetch for Company Number: %s ", companyNumber ) ) );
                }
            } catch (Exception exception) {
                throw new InternalServerErrorRuntimeException( String.format( "Unexpected error while fetching for Company Number: %s", companyNumber ), new Exception( String.format( "Unexpected error while fetching for Company Number: %s", companyNumber ) ) );
            }
        };
    }


    public Supplier<String> buildUpdateStatusRequest(final String xRequestId, final String associationId, final StatusEnum statusEnum) {
        final var request = accountsAssociationEndpoint.buildUpdateStatusRequest(xRequestId, associationId, statusEnum);
        return () -> {
            try {
                LOGGER.debug(String.format("Updating status for association ID %s with status %s", associationId, statusEnum), null);
                request.execute();
                return associationId;
            } catch (ApiErrorResponseException exception) {
                if (exception.getStatusCode() == 404) {
                    throw new NotFoundRuntimeException( String.format( "Association not found for ID: %s ", associationId ), new Exception( String.format( "Association not found for ID: %s ", associationId ) ) );
                } else {
                    throw new InternalServerErrorRuntimeException( String.format( "Failed to update status for association ID: %s", associationId ), new Exception( String.format( "Failed to update status for association ID: %s ", associationId ) ) );
                }
            } catch (Exception exception) {
                throw new InternalServerErrorRuntimeException( String.format( "Unexpected error while updating status for association ID: %s", associationId ), new Exception( String.format( "Unexpected error while updating status for association ID: %s ", associationId ) ) );
            }
        };
    }
}
