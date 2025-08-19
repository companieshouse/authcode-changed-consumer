package uk.gov.companieshouse.authcode.changed.service;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.verification.Times;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.associations.model.AssociationsList;
import uk.gov.companieshouse.api.accounts.associations.model.Links;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.accountsassociation.request.PrivateAccountsAssociationForCompanyGet;
import uk.gov.companieshouse.api.handler.accountsassociation.request.PrivateAccountsAssociationUpdateStatusPatch;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.authcode.changed.common.TestDataManager;
import uk.gov.companieshouse.authcode.changed.exceptions.InternalServerErrorRuntimeException;
import uk.gov.companieshouse.authcode.changed.rest.AccountsAssociationEndpoint;

@ExtendWith(MockitoExtension.class)
@Tag("unit-test")
class AssociationServiceTest {

    @Mock
    private AccountsAssociationEndpoint accountsAssociationEndpoint;

    @Mock
    private PrivateAccountsAssociationForCompanyGet privateAccountsAssociationForCompanyGet;

    @Mock
    private PrivateAccountsAssociationUpdateStatusPatch privateAccountsAssociationUpdateStatusPatch;

    @Mock
    private AssociationService associationService;

    private static final TestDataManager testDataManager = TestDataManager.getInstance();

    @Test
    void getAssociationDetailsForCompanyWithNullInputReturnsInternalServerError() throws ApiErrorResponseException, URIValidationException {
        Mockito.doReturn( privateAccountsAssociationForCompanyGet ).when( accountsAssociationEndpoint ).createGetAssociationsForCompanyRequest( Mockito.isNull(), Mockito.anyBoolean(), Mockito.anyInt(), Mockito.anyInt() );
        Mockito.doThrow( NullPointerException.class ).when( privateAccountsAssociationForCompanyGet ).execute();
        Assertions.assertThrows( InternalServerErrorRuntimeException.class , () -> associationService.fetchAssociationDetails( null ) );
    }

    @Test
    void getAssociationDetailsForCompanyWithMalformedInputReturnsInternalServerError() throws ApiErrorResponseException, URIValidationException {
        Mockito.doReturn( privateAccountsAssociationForCompanyGet ).when( accountsAssociationEndpoint ).createGetAssociationsForCompanyRequest( Mockito.isNull(), Mockito.anyBoolean(), Mockito.anyInt(), Mockito.anyInt() );
        Mockito.doThrow( new URIValidationException("Uri incorrectly formated") ) .when( privateAccountsAssociationForCompanyGet ).execute();
        Assertions.assertThrows( InternalServerErrorRuntimeException.class , () -> associationService.fetchAssociationDetails( "$$$" ) );
    }

//// mock details that come back from association api.
//// also need to mockito verify to see what we're sending to associaiton api is true.
//// verify we sent the right variables
//
//
//// let's say in our mock we have 3 associations.
//// we need to check that our patch gets sent 3 times.

    @Test
    void getAssociationDetailsForCompanyWithValidInputReturnsAssociationsList() throws ApiErrorResponseException, URIValidationException {
        final var associationsList = new AssociationsList()
                .totalResults( 1 ).totalPages( 1 ).pageNumber( 0 ).itemsPerPage( 15 )
                .links( new Links().self("").next("") )
                .items(List.of( testDataManager.getAssociation1(), testDataManager.getAssociation2(), testDataManager.getAssociation3() ));

        Mockito.doReturn( new ApiResponse<>( 200, null, associationsList ) ).when( accountsAssociationEndpoint ).createGetAssociationsForCompanyRequest( Mockito.anyString(), Mockito.anyBoolean(), Mockito.anyInt(), Mockito.anyInt() );
        Mockito.doReturn( new ApiResponse<>( 200, null) ).when( accountsAssociationEndpoint ).createUpdateStatusRequest( Mockito.anyString(), Mockito.any() );

        associationService.setCompanyAssociationToUnauthorised("MKCOMP001");
        Mockito.verify( accountsAssociationEndpoint, new Times(3) ).createUpdateStatusRequest(Mockito.anyString(), Mockito.any());

    }

}
