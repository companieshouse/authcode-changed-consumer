package uk.gov.companieshouse.authcode.changed.rest;

import java.util.function.Supplier;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.accounts.associations.model.RequestBodyPut.StatusEnum;
import uk.gov.companieshouse.api.handler.accountsassociation.request.PrivateAccountsAssociationForCompanyGet;
import uk.gov.companieshouse.api.handler.accountsassociation.request.PrivateAccountsAssociationUpdateStatusPatch;

@Service
public class AccountsAssociationEndpoint {

    private final Supplier<InternalApiClient> accountApiClientSupplier;

    public AccountsAssociationEndpoint( final Supplier<InternalApiClient> accountApiClientSupplier ) {
        this.accountApiClientSupplier = accountApiClientSupplier;
    }

    public PrivateAccountsAssociationForCompanyGet buildGetAssociationsForCompanyRequest( final String xRequestId, final String companyNumber, final boolean includeRemoved, final int pageIndex, final int itemsPerPage ){
        final var url = String.format( "/associations/companies/%s", companyNumber );
        final var client = accountApiClientSupplier.get();
        client.getHttpClient().setRequestId( xRequestId );
        return client.privateAccountsAssociationResourceHandler()
                .getAssociationsForCompany( url, includeRemoved, pageIndex, itemsPerPage );
    }

    public PrivateAccountsAssociationUpdateStatusPatch buildUpdateStatusRequest( final String xRequestId, final String associationId, final StatusEnum statusEnum ){
        final var updateStatusUrl = String.format( "/associations/%s", associationId );
        final var client = accountApiClientSupplier.get();
        client.getHttpClient().setRequestId( xRequestId );
        return client.privateAccountsAssociationResourceHandler()
                .updateAssociationStatusForId( updateStatusUrl, statusEnum );
    }
}
