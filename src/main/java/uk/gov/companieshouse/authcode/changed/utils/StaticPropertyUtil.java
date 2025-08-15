package uk.gov.companieshouse.authcode.changed.utils;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class StaticPropertyUtil {

    @Value( "${spring.application.name}" )

    public static String APPLICATION_NAMESPACE;

    private String applicationNameSpace;

    @PostConstruct
    public void init() {
        StaticPropertyUtil.APPLICATION_NAMESPACE = applicationNameSpace;
    }

}
