package uk.gov.companieshouse.authcode.changed.exceptions;

import static uk.gov.companieshouse.authcode.changed.utils.LoggingUtil.LOGGER;

public class ForbiddenRuntimeException extends RuntimeException {

    public ForbiddenRuntimeException(final String exceptionMessage, final Exception loggingMessage) {
        super(exceptionMessage);
        LOGGER.error(loggingMessage, null);
    }
}