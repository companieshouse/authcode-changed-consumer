package uk.gov.companieshouse.authcode.changed.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.accounts.associations.model.AssociationsList;
import uk.gov.companieshouse.api.accounts.associations.model.RequestBodyPut.StatusEnum;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.accountsassociation.request.PrivateAccountsAssociationForCompanyGet;
import uk.gov.companieshouse.api.handler.accountsassociation.request.PrivateAccountsAssociationUpdateStatusPatch;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.authcode.changed.utils.ApiClientUtil;

@Service
public class AccountsAssociationEndpoint {

    @Value("${account.api.url}")
    private String accountApiUrl;

    private final ApiClientUtil apiClientUtil;


    public AccountsAssociationEndpoint(ApiClientUtil apiClientUtil) { this.apiClientUtil = apiClientUtil; }


    public PrivateAccountsAssociationForCompanyGet buildGetAssociationsForCompanyRequest( String companyNumber, Boolean includeRemoved, int pageIndex, int itemsPerPage) {
        final var url = String.format("/associations/companies/%s", companyNumber);
        return apiClientUtil.getInternalApiClient(accountApiUrl)
                .privateAccountsAssociationResourceHandler()
                .getAssociationsForCompany(url, includeRemoved, pageIndex, itemsPerPage);
    }

    public PrivateAccountsAssociationUpdateStatusPatch createUpdateStatusRequest(final String associationId, final StatusEnum statusEnum) throws ApiErrorResponseException, URIValidationException {
        final var updateStatusUrl = String.format("/associations/%s", associationId);
        return apiClientUtil.getInternalApiClient(accountApiUrl)
                .privateAccountsAssociationResourceHandler()
                .updateAssociationStatusForId(updateStatusUrl, statusEnum);
    }
}
