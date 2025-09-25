package uk.gov.companieshouse.authcode.changed.config;

import static uk.gov.companieshouse.authcode.changed.model.Constants.UNKNOWN;

import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.http.ApiKeyHttpClient;

@Configuration
public class ApiClientConfig {

    private final String internalApiKey;
    private final String accountApiUrl;

    public ApiClientConfig( @Value( "${chs.internal.api.key}" ) final String internalApiKey, @Value( "${account.api.url}" ) final String accountApiUrl ){
        this.internalApiKey = internalApiKey;
        this.accountApiUrl = accountApiUrl;
    }

    @Bean
    public Supplier<InternalApiClient> accountApiClientSupplier() {
        return () -> {
            final var apiKeyHttpClient = new ApiKeyHttpClient( internalApiKey );
            apiKeyHttpClient.setRequestId( UNKNOWN );

            final var internalApiClient = new InternalApiClient( apiKeyHttpClient );
            internalApiClient.setInternalBasePath( accountApiUrl );

            return internalApiClient;
        };
    }

}
