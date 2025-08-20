package uk.gov.companieshouse.authcode.changed.interceptor;

import static uk.gov.companieshouse.authcode.changed.utils.LoggingUtil.LOGGER;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import uk.gov.companieshouse.logging.util.RequestLogger;

@Component
public class LoggingInterceptor implements HandlerInterceptor, RequestLogger {

    @Override
    public boolean preHandle( final HttpServletRequest request, final HttpServletResponse response, final Object handler ) {
        logStartRequestProcessing( request, LOGGER );
        return true;
    }

    @Override
    public void postHandle( final HttpServletRequest request, final HttpServletResponse response, final Object handler, final ModelAndView modelAndView ) {
        logEndRequestProcessing( request, response, LOGGER );
    }

}