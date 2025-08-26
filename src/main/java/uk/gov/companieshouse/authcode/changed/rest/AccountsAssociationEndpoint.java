package uk.gov.companieshouse.authcode.changed.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.accounts.associations.model.RequestBodyPut.StatusEnum;
import uk.gov.companieshouse.api.handler.accountsassociation.request.PrivateAccountsAssociationForCompanyGet;
import uk.gov.companieshouse.api.handler.accountsassociation.request.PrivateAccountsAssociationUpdateStatusPatch;
import uk.gov.companieshouse.authcode.changed.utils.ApiClientUtil;

@Service
public class AccountsAssociationEndpoint {

    @Value("${account.api.url}")
    private String accountApiUrl;

    private final ApiClientUtil apiClientUtil;


    public AccountsAssociationEndpoint(final ApiClientUtil apiClientUtil) {
        this.apiClientUtil = apiClientUtil;
    }

    public PrivateAccountsAssociationForCompanyGet buildGetAssociationsForCompanyRequest(final String companyNumber, final boolean includeRemoved, final int pageIndex, final int itemsPerPage){
        final var url = String.format( "/associations/companies/%s", companyNumber );
        return apiClientUtil.getInternalApiClient(accountApiUrl)
                .privateAccountsAssociationResourceHandler()
                .getAssociationsForCompany(url, includeRemoved, pageIndex, itemsPerPage);
    }

    public PrivateAccountsAssociationUpdateStatusPatch buildUpdateStatusRequest(final String associationId, final StatusEnum statusEnum){
        final var updateStatusUrl = String.format("/associations/%s", associationId);
        return apiClientUtil.getInternalApiClient(accountApiUrl)
                .privateAccountsAssociationResourceHandler()
                .updateAssociationStatusForId(updateStatusUrl, statusEnum);
    }
}
