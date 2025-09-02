package uk.gov.companieshouse.authcode.changed.utils;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class StaticPropertyUtil {

    @Value("${spring.application.name}")
    private String applicationNameSpace;

    @Value("${chs.kafka.association.items_per_page}")
    private int kafkaAssociationItemsPerPage;

    public static int KAFKA_ASSOCIATION_ITEMS_PER_PAGE;

    public static String APPLICATION_NAMESPACE;

    @PostConstruct
    public void init() {
        StaticPropertyUtil.APPLICATION_NAMESPACE = applicationNameSpace;
        StaticPropertyUtil.KAFKA_ASSOCIATION_ITEMS_PER_PAGE = kafkaAssociationItemsPerPage;
    }
}
