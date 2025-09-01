package uk.gov.companieshouse.authcode.changed.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.sdk.manager.ApiSdkManager;

@Configuration
public class ApiClientConfig {

    @Bean
    public InternalApiClient getInternalApiClient() {
        return ApiSdkManager.getInternalSDK();
    }
}
