package uk.gov.companieshouse.authcode.changed.utils;

import static uk.gov.companieshouse.authcode.changed.models.Constants.UNKNOWN;
import static uk.gov.companieshouse.authcode.changed.models.context.RequestContext.getRequestContext;

import java.util.HashSet;
import java.util.Optional;
import java.util.function.Function;
import uk.gov.companieshouse.api.accounts.user.model.User;
import uk.gov.companieshouse.authcode.changed.models.context.RequestContextData;

public final class RequestContextUtil {

    private RequestContextUtil(){}

    private static <T> T getFieldFromRequestContext( final Function<RequestContextData, T> getterMethod, final T defaultValue ){
        return Optional.ofNullable( getRequestContext() ).map( getterMethod ).orElse( defaultValue );
    }

    public static String getXRequestId(){
        return getFieldFromRequestContext( RequestContextData::getXRequestId, UNKNOWN );
    }
//
//    public static String getEricIdentity(){
//        return getFieldFromRequestContext( RequestContextData::getEricIdentity, UNKNOWN );
//    }
//
//    public static String getEricIdentityType(){
//        return getFieldFromRequestContext( RequestContextData::getEricIdentityType, UNKNOWN );
//    }
//
//    public static boolean hasAdminPrivilege( final String privilege ){
//        return getFieldFromRequestContext( RequestContextData::getAdminPrivileges, new HashSet<>() ).contains( privilege );
//    }


}