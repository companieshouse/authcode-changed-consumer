package uk.gov.companieshouse.authcode.changed.config;

import static org.springframework.http.HttpMethod.GET;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(
                request -> request.requestMatchers(GET, "/authcode-changed-consumer/healthcheck")
                        .permitAll().anyRequest().denyAll()).build();
    }
}