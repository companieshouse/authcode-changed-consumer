package uk.gov.companieshouse.authcode.changed.exceptions;

import static uk.gov.companieshouse.authcode.changed.utils.LoggingUtil.LOGGER;
import static uk.gov.companieshouse.authcode.changed.utils.RequestContextUtil.getXRequestId;

public class ForbiddenRuntimeException extends RuntimeException {

    public ForbiddenRuntimeException( final String exceptionMessage, final Exception loggingMessage ) {
        super( exceptionMessage );
        LOGGER.errorContext( getXRequestId(), loggingMessage, null );
    }
}