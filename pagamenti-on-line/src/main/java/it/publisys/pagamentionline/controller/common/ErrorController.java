package it.publisys.pagamentionline.controller.common;

import it.publisys.pagamentionline.RequestMappings;
import it.publisys.pagamentionline.ViewMappings;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author stoma
 */
@Controller
@RequestMapping(RequestMappings.ERROR)
public class ErrorController {

    @RequestMapping(RequestMappings.ERROR_404)
    public String error404() {
        return ViewMappings.ERROR_404;
    }

    @RequestMapping(RequestMappings.ERROR_500)
    public String error500() {
        return ViewMappings.ERROR_500;
    }

    @RequestMapping(value = RequestMappings.ACCESS_DENIED)
    public String denied(ModelMap model) {
        return ViewMappings.ACCESS_DENIED;
    }
}
