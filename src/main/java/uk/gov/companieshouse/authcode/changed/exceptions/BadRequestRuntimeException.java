package uk.gov.companieshouse.authcode.changed.exceptions;

import static uk.gov.companieshouse.authcode.changed.utils.LoggingUtil.LOGGER;

public class BadRequestRuntimeException extends RuntimeException {

    public BadRequestRuntimeException(final String exceptionMessage, final Exception loggingMessage) {
        super(exceptionMessage);
        LOGGER.error(loggingMessage, null);
    }
}
