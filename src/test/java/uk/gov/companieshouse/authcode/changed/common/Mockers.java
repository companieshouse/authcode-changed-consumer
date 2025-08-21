package uk.gov.companieshouse.authcode.changed.common;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.List;
import org.mockito.Mockito;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import uk.gov.companieshouse.authcode.changed.exceptions.NotFoundRuntimeException;

import uk.gov.companieshouse.api.accounts.user.model.User;
import uk.gov.companieshouse.api.accounts.user.model.UsersList;
import uk.gov.companieshouse.authcode.changed.service.AssociationService;


public class Mockers {

    private static final TestDataManager testDataManager = TestDataManager.getInstance();

    private final WebClient webClient;
    private final AssociationService associationService;

    public Mockers( final WebClient webClient, final AssociationService associationService) {
        this.webClient = webClient;
        this.associationService = associationService;
    }

    private void mockWebClientSuccessResponse( final String uri, final Mono<String> jsonResponse ){
        final var requestHeadersUriSpec = Mockito.mock( WebClient.RequestHeadersUriSpec.class );
        final var requestHeadersSpec = Mockito.mock( WebClient.RequestHeadersSpec.class );
        final var responseSpec = Mockito.mock( WebClient.ResponseSpec.class );

        Mockito.doReturn( requestHeadersUriSpec ).when(webClient).get();
        Mockito.doReturn( requestHeadersSpec ).when( requestHeadersUriSpec ).uri( uri );
        Mockito.doReturn( responseSpec ).when( requestHeadersSpec ).retrieve();
        Mockito.doReturn( jsonResponse ).when( responseSpec ).bodyToMono( String.class );
    }

    public void mockWebClientForFetchCompanyDetails( final String... companyNumbers ) throws JsonProcessingException {
        for ( final String companyNumber: companyNumbers ){
            final var user = testDataManager.getAssociation1();
            final var uri = String.format( "/associations/companies/%s", companyNumber );
            final var jsonResponse = new ObjectMapper().writeValueAsString( user );
            mockWebClientSuccessResponse( uri, Mono.just( jsonResponse ) );
        }
    }

    private void mockWebClientErrorResponse( final String uri, int responseCode ){
        final var requestHeadersUriSpec = Mockito.mock( WebClient.RequestHeadersUriSpec.class );
        final var requestHeadersSpec = Mockito.mock( WebClient.RequestHeadersSpec.class );
        final var responseSpec = Mockito.mock( WebClient.ResponseSpec.class );

        Mockito.doReturn( requestHeadersUriSpec ).when(webClient).get();
        Mockito.doReturn( requestHeadersSpec ).when( requestHeadersUriSpec ).uri( uri );
        Mockito.doReturn( responseSpec ).when( requestHeadersSpec ).retrieve();
        Mockito.doReturn( Mono.error( new WebClientResponseException( responseCode, "Error", null, null, null ) ) ).when( responseSpec ).bodyToMono( String.class );
    }

    public void mockWebClientForFetchCompanyDetailsErrorResponse( final String companyNumber, int responseCode ){
        final var uri = String.format( "/associations/companies/%s", companyNumber );
        mockWebClientErrorResponse( uri, responseCode );
    }

    public void mockWebClientForFetchUserDetailsErrorResponse( final String companyNumber, int responseCode ){
        final var uri = String.format( "/associations/companies/%s", companyNumber );
        mockWebClientErrorResponse( uri, responseCode );
    }

    public void mockWebClientForFetchCompanyDetailsNotFound( final String... companyNumbers ){
        for ( final String companyNumber: companyNumbers ){
            mockWebClientForFetchUserDetailsErrorResponse( companyNumber,404 );
        }
    }

    private void mockWebClientJsonParsingError( final String uri ){
        final var requestHeadersUriSpec = Mockito.mock( WebClient.RequestHeadersUriSpec.class );
        final var requestHeadersSpec = Mockito.mock( WebClient.RequestHeadersSpec.class );
        final var responseSpec = Mockito.mock( WebClient.ResponseSpec.class );

        Mockito.doReturn( requestHeadersUriSpec ).when(webClient).get();
        Mockito.doReturn( requestHeadersSpec ).when( requestHeadersUriSpec ).uri( uri );
        Mockito.doReturn( responseSpec ).when( requestHeadersSpec ).retrieve();
        Mockito.doReturn( Mono.just( "}{" ) ).when( responseSpec ).bodyToMono( String.class );
    }

    public void mockWebClientForFetchCompanyDetailsJsonParsingError( final String companyNumber ){
        final var uri = String.format( "/users/%s", companyNumber );
        mockWebClientJsonParsingError( uri );
    }

    public void mockWebClientForFetchCompanyProfileErrorResponse( final String companyNumber, int responseCode ){
        final var uri = String.format( "/company/%s/company-detail", companyNumber );
        mockWebClientErrorResponse( uri, responseCode );
    }

    public void mockWebClientForFetchCompanyProfileNotFound( final String... companyNumbers ){
        for ( final String companyNumber: companyNumbers ){
            mockWebClientForFetchCompanyProfileErrorResponse( companyNumber, 404 );
        }
    }

    public void mockWebClientForFetchCompanyProfileJsonParsingError( final String companyNumber ){
        final var uri = String.format( "/company/%s/company-detail", companyNumber );
        mockWebClientJsonParsingError( uri );
    }

    public void mockAssociationServiceFetchCompanyDetails( final String... companyNumbers ){
        for ( final String companyNumber: companyNumbers ){
            final var userDetails = testDataManager.getAssociation1();
            Mockito.doReturn( userDetails ).when(associationService).fetchAssociationDetails( companyNumber);
        }
    }

    public void mockAssociationServiceToFetchCompanyDetailsRequest( final String... companyNumbers ){
        for ( final String companyNumber: companyNumbers ){
            final var userDetails = testDataManager.getAssociation1();
            Mockito.doReturn( Mono.just( userDetails ) ).when(associationService).fetchAssociationDetails( companyNumber );
        }
    }

    public void mockAssociationServiceFetchCompanyDetailsNotFound( final String... companyNumbers ){
        for ( final String companyNumber: companyNumbers ){
            Mockito.doThrow( new NotFoundRuntimeException( "Not found.", new Exception( "Not found." ) ) ).when(
                    associationService).fetchAssociationDetails( companyNumber );
        }
    }

}
