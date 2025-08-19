package uk.gov.companieshouse.authcode.changed.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.accounts.associations.model.AssociationsList;
import uk.gov.companieshouse.api.accounts.associations.model.RequestBodyPut.StatusEnum;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.authcode.changed.utils.ApiClientUtil;

@Service
public class AccountsAssociationEndpoint {

    @Value("${account.api.url}")
    private String accountApiUrl;

    private final ApiClientUtil apiClientUtil;


    public AccountsAssociationEndpoint(ApiClientUtil apiClientUtil) { this.apiClientUtil = apiClientUtil; }

    public ApiResponse<AssociationsList> createGetAssociationsForCompanyRequest(final String companyNumber, final Boolean includeRemoved,  final int pageIndex, final int itemsPerPage) throws ApiErrorResponseException, URIValidationException {
        final var getAssociationsForCompanyUrl = String.format("/associations/companies/%s", companyNumber);
        return apiClientUtil.getInternalApiClient(accountApiUrl)
                .privateAccountsAssociationResourceHandler()
                .getAssociationsForCompany(getAssociationsForCompanyUrl, includeRemoved, pageIndex, itemsPerPage )
                .execute();
    }

    public ApiResponse<Void> createUpdateStatusRequest(final String associationId, final StatusEnum statusEnum) throws ApiErrorResponseException, URIValidationException {
        final var updateStatusUrl = String.format("/associations/%s", associationId);
        return apiClientUtil.getInternalApiClient(accountApiUrl)
                .privateAccountsAssociationResourceHandler()
                .updateAssociationStatusForId(updateStatusUrl, statusEnum)
                .execute();
    }


}
