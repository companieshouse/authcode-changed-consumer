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

    public Association createAssociationFromCompanyNumber(String companyNumber, User userDetails) {
        final var companyDetail = new CompanyDetails()
                .companyNumber(companyNumber)
                .companyName("Mushroom Kingdom")
                .companyStatus("Active");

        return createAssociation(userDetails.getUserId(), userDetails, companyDetail, StatusEnum.AWAITING_APPROVAL);
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
