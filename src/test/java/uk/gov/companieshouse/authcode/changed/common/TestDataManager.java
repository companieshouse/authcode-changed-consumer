package uk.gov.companieshouse.authcode.changed.common;

import java.util.Objects;
import java.util.Optional;
import uk.gov.companieshouse.api.accounts.associations.model.Association;
import uk.gov.companieshouse.api.accounts.associations.model.Association.StatusEnum;
import uk.gov.companieshouse.api.accounts.user.model.User;
import uk.gov.companieshouse.api.company.CompanyDetails;

public class TestDataManager {

    private static TestDataManager instance = null;

    public static TestDataManager getInstance() {
        if (Objects.isNull(instance)) {
            instance = new TestDataManager();
        }
        return instance;
    }

    public Association getAssociation1() {
        final var userDetails = new User().userId("MKUser001")
                .email("mario@mushroom.kingdom");
        final var companyDetail = new CompanyDetails().companyNumber("MKCOMP001")
                .companyName("Mushroom Kingdom")
                .companyStatus("Active");

        return createAssociation("MKUser001", userDetails, companyDetail, StatusEnum.MIGRATED);
    }

    public Association getAssociation2() {
        final var userDetails = new User().userId("MKUser002")
                .email("luigi@mushroom.kingdom");
        final var companyDetail = new CompanyDetails().companyNumber("MKCOMP001")
                .companyName("Mushroom Kingdom")
                .companyStatus("Active");

        return createAssociation("MKUser002", userDetails, companyDetail, StatusEnum.CONFIRMED);
    }

    public Association getAssociation3() {
        final var userDetails = new User().userId("MKUser003")
                .email("peach@mushroom.kingdom");
        final var companyDetail = new CompanyDetails().companyNumber("MKCOMP001")
                .companyName("Mushroom Kingdom")
                .companyStatus("Active");

        return createAssociation("MKUser003", userDetails, companyDetail, StatusEnum.AWAITING_APPROVAL);
    }

    public Association createAssociationFromCompanyNumber(String companyNumber) {
        final var userDetails = new User().userId("MKUser003")
                .email("peach@mushroom.kingdom");
        final var companyDetail = new CompanyDetails().companyNumber(companyNumber)
                .companyName("Mushroom Kingdom")
                .companyStatus("Active");

        return createAssociation("MKUser003", userDetails, companyDetail, StatusEnum.AWAITING_APPROVAL);
    }

    public Association createAssociation(final String id, final User user, final CompanyDetails companyDetail,
            final StatusEnum statusEnum) {

        final var association = new Association();
        association.setId(id);
        association.setUserId(user.getUserId());
        association.setUserEmail(user.getEmail());
        association.setDisplayName(Optional.ofNullable(user.getDisplayName())
                .orElse("Not provided"));
        association.setCompanyNumber(companyDetail.getCompanyNumber());
        association.setCompanyName(companyDetail.getCompanyNumber());
        association.setCompanyStatus(companyDetail.getCompanyStatus());
        association.setStatus(statusEnum);
        return association;
    }

}
