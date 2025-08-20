package uk.gov.companieshouse.authcode.changed.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.gov.companieshouse.authcode.changed.interceptor.LoggingInterceptor;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    private final LoggingInterceptor loggingInterceptor;

    public InterceptorConfig( final LoggingInterceptor loggingInterceptor ) {
        this.loggingInterceptor = loggingInterceptor;
    }

    @Override
    public void addInterceptors( final InterceptorRegistry registry ) {
        registry.addInterceptor( loggingInterceptor );
    }

}