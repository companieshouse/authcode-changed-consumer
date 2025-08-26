package uk.gov.companieshouse.authcode.changed.service;

import java.util.function.Supplier;
import uk.gov.companieshouse.api.accounts.associations.model.AssociationsList;
import uk.gov.companieshouse.api.accounts.associations.model.RequestBodyPut.StatusEnum;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
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

    public Supplier<AssociationsList> buildFetchAssociationsForCompanyRequest(final String companyNumber, final boolean includeRemoved, final int pageIndex, final int itemsPerPage) throws ApiErrorResponseException, URIValidationException {
        final var request = accountsAssociationEndpoint.buildGetAssociationsForCompanyRequest(companyNumber, includeRemoved, pageIndex, itemsPerPage);
        return () -> {
            try {
                LOGGER.debug(String.format("Sending request to account-association-api: GET /associations/companies/{company_number}. Attempting to fetch Association details for company %s ", companyNumber), null);
                return request.execute().getData();
            } catch (ApiErrorResponseException exception) {
                if (exception.getStatusCode() == 404) {
                    LOGGER.error(new Exception(String.format("Association not found for Company Number: %s ", companyNumber), exception), null);
                    throw new NotFoundRuntimeException(String.format("Association not found for Company Number: %s ", companyNumber), exception);
                } else {
                    LOGGER.error(new Exception(String.format("Failed to fetch for Company Number: %s ", companyNumber), exception), null);
                    throw new InternalServerErrorRuntimeException(String.format("Failed to fetch for Company Number: %s", companyNumber), exception);
                }
            } catch (Exception exception) {
                LOGGER.error(new Exception(String.format("Failed to fetch for Company Number: %s", companyNumber), exception), null);
                throw new InternalServerErrorRuntimeException(String.format("Unexpected error while fetching for Company Number: %s", companyNumber), exception);
            }
        };
    }


    public Supplier<String> buildUpdateStatusRequest(final String associationId, final StatusEnum statusEnum) throws ApiErrorResponseException, URIValidationException {
        final var request = accountsAssociationEndpoint.buildUpdateStatusRequest(associationId, statusEnum);
        return () -> {
            try {
                LOGGER.debug(String.format("Updating status for association ID %s with status %s", associationId, statusEnum), null);
                request.execute();
                return associationId;
            } catch (ApiErrorResponseException exception) {
                if (exception.getStatusCode() == 404) {
                    LOGGER.error(new Exception(String.format("Association not found for ID: %s ", associationId), exception), null);
                    throw new NotFoundRuntimeException(String.format("Association not found for ID: %s ", associationId), exception);
                } else {
                    LOGGER.error(new Exception(String.format("Failed to update status for association ID: %s ", associationId), exception), null);
                    throw new InternalServerErrorRuntimeException(String.format("Failed to update status for association ID: %s", associationId), exception);
                }
            } catch (Exception exception) {
                LOGGER.error(new Exception(String.format("Failed to update status for association ID: %s", associationId), exception), null);
                throw new InternalServerErrorRuntimeException(String.format("Unexpected error while updating status for association ID: %s", associationId), exception);
            }
        };
    }
}
