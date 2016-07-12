package it.publisys.pagamentionline.controller.common;

import it.publisys.ibasho.guard.LogoutGuard;
import it.publisys.pagamentionline.ModelMappings;
import it.publisys.pagamentionline.RequestMappings;
import it.publisys.pagamentionline.ViewMappings;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author mcolucci
 */
@Controller
public class LoginController {

    private static final Logger _log = LoggerFactory.getLogger(LoginController.class);

    @RequestMapping(value = RequestMappings.LOGIN_IMS,
        method = RequestMethod.GET)
    public String loginIms(@RequestParam("u") String u,
                           @RequestParam("key") String key,
                           ModelMap model) {
        model.addAttribute("uname", u);
        model.addAttribute("ukey", key);
        return ViewMappings.LOGIN_IMS;
    }

    @RequestMapping(value = RequestMappings.LOGIN, method = RequestMethod.GET)
    public String login(ModelMap model) {
        return ViewMappings.LOGIN;
    }

    @RequestMapping(value = RequestMappings.LOGIN_FAILED,
        method = RequestMethod.GET)
    public String loginFailed(ModelMap model) {
        model.addAttribute(ModelMappings.ERROR, true);
        return ViewMappings.LOGIN_FAILED;
    }

    @RequestMapping(value = RequestMappings.LOGOUT, method = RequestMethod.GET)
    public String logout(ModelMap model,
                         HttpServletRequest request) {
        try {
            LogoutGuard.logout(request);
        } catch (Exception ignored) {
        }

        return ViewMappings.LOGOUT; //ViewMappings.REDIRECT + RequestMappings.INDEX;
    }

}
