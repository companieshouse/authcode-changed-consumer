package uk.gov.companieshouse.authcode.changed.service;

import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.sdk.manager.ApiSdkManager;

@Service
public class ApiClientService {

    public InternalApiClient getInternalApiClient( String baseUrl) {
        final var internalApiClient = ApiSdkManager.getInternalSDK();
        internalApiClient.setInternalBasePath( baseUrl );
        return internalApiClient;
    }
}
