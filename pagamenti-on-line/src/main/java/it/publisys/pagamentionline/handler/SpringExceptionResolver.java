package it.publisys.pagamentionline.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

/**
 *
 * @author mcolucci
 */
public class SpringExceptionResolver
    extends SimpleMappingExceptionResolver {

    private static final Logger _log = LoggerFactory.getLogger(SpringExceptionResolver.class);

    @Override
    protected ModelAndView doResolveException(HttpServletRequest request,
                                              HttpServletResponse response,
                                              Object handler, Exception ex) {

        _log.error("A " + ex.getClass().getSimpleName() + " has occured in the application", ex);
        return super.doResolveException(request, response, handler, ex);
    }

}
