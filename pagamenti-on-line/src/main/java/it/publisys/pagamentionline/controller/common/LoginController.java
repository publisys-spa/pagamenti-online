package it.publisys.pagamentionline.controller.common;

import it.publisys.ibasho.guard.LogoutGuard;
import it.publisys.pagamentionline.ModelMappings;
import it.publisys.pagamentionline.RequestMappings;
import it.publisys.pagamentionline.ViewMappings;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.publisys.pagamentionline.controller.impl.LoginImpl;
import it.publisys.pagamentionline.util.sec.AuthorityUtil;
import it.publisys.pagamentionline.util.sec.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import sun.security.jgss.LoginConfigImpl;

import java.util.Map;
import java.util.UUID;

/**
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

    @RequestMapping(value = RequestMappings.SPID, method = RequestMethod.GET)
    public String spid(final HttpServletRequest req, HttpServletResponse response) {
        if (SecurityUtil.isAuthenticated()) {
            if (AuthorityUtil.isAdminLogged()) {
                return ViewMappings.HOME;
            }
        } else {
            try {
                java.util.Map mappa = getShibbAttributes(req);

                LoginImpl loginImpl = new LoginImpl();
                loginImpl.initialize(req, response, mappa);

                if (SecurityUtil.isAuthenticated()) {
                    if (AuthorityUtil.isAdminLogged()) {
                        return ViewMappings.HOME;
                    }
                }
                return ViewMappings.ACCESS_DENIED;
            } catch (Exception ex) {
                ex.printStackTrace(System.out);
            }
            return ViewMappings.HOME;
        }
        return ViewMappings.ACCESS_DENIED;
    }

    @RequestMapping(value = RequestMappings.LOGIN, method = RequestMethod.GET)
    public String login() {
        //  return ViewMappings.REDIRECT + RequestMappings.SPID;
        return ViewMappings.LOGIN;
    }

    @RequestMapping(value = RequestMappings.LOGIN_FAILED,
            method = RequestMethod.GET)
    public String loginFailed(ModelMap model) {
        model.addAttribute(ModelMappings.ERROR, true);
        return ViewMappings.LOGIN_FAILED;
    }

    @RequestMapping(value = RequestMappings.LOGOUT, method = RequestMethod.GET)
    public String logout( HttpServletResponse response,
                         HttpServletRequest request) {
        try {
            LogoutGuard.logout(request);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            CookieClearingLogoutHandler cookieClearingLogoutHandler = new CookieClearingLogoutHandler(AbstractRememberMeServices.SPRING_SECURITY_REMEMBER_ME_COOKIE_KEY);
            SecurityContextLogoutHandler securityContextLogoutHandler = new SecurityContextLogoutHandler();
            cookieClearingLogoutHandler.logout(request, response, authentication);
            securityContextLogoutHandler.logout(request, response, authentication);
        } catch (Exception ignored) {
        }

        return ViewMappings.LOGOUT;
    }

    //Mappa attributi spid
    protected Map getShibbAttributes(HttpServletRequest request) {

        java.util.Map mappa = new java.util.HashMap();

        mappa.put("uid", request.getHeader("shibb-uid"));
        mappa.put("fiscalcode", request.getHeader("shib-fiscalNumber"));
        mappa.put("username", request.getHeader("shib-fiscalNumber"));
        mappa.put("lastname", request.getHeader("shib-familyName"));
        mappa.put("firstname", request.getHeader("shib-name"));
        mappa.put("email", request.getHeader("shib-email"));
        mappa.put("CATEGORY", request.getHeader("shibb-CATEGORY"));
        mappa.put("keyrequest", UUID.randomUUID().toString());

        return mappa;
    }

}
