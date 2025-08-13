package uk.gov.companieshouse.authcode.changed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import uk.gov.companieshouse.authcode.changed.utils.StaticPropertyUtil;

@SpringBootApplication
public class AuthcodeChangedConsumerApplication {

    final StaticPropertyUtil staticPropertyUtil;

    @Autowired
    public AuthcodeChangedConsumerApplication( final StaticPropertyUtil staticPropertyUtil ) {
        this.staticPropertyUtil = staticPropertyUtil;
    }

    public static void main(String[] args) {
        SpringApplication.run(AuthcodeChangedConsumerApplication.class, args);
    }
}