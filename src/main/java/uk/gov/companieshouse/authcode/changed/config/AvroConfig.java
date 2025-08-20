package uk.gov.companieshouse.authcode.changed.config;

import consumer.deserialization.AvroDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.companieshouse.authcode.cancellation.AuthCodeCancellation;

@Configuration
public class AvroConfig {

    @Bean
    public AvroDeserializer<AuthCodeCancellation> authCodeCancellationAvroDeserializer() {
        return new AvroDeserializer<>(AuthCodeCancellation.class);
    }
}
