package it.publisys.pagamentionline.controller.impl;



import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

import it.publisys.ibasho.guard.IbashoInitialize;
import it.publisys.pagamentionline.ModelMappings;
import it.publisys.pagamentionline.ctx.SpringApplicationContext;
import it.publisys.pagamentionline.domain.user.User;
import it.publisys.pagamentionline.domain.user.UserRole;
import it.publisys.pagamentionline.service.login.LoginUserDetailsService;
import it.publisys.pagamentionline.util.sec.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Created by micaputo on 11/11/2016.
 */
public class LoginImpl  implements IbashoInitialize {


    private static final Logger _log = LoggerFactory.getLogger(LoginImpl.class.getName());

    private String message;

    @Override
    public boolean initialize(HttpServletRequest request,
                              HttpServletResponse response, Map attributes)
            throws Exception {
        boolean esito = true;

        String _method = "initialize";
        _log.debug(_method + " - start");
        try {
            HttpSession session = request.getSession(false);
            if (null == (session.getAttribute(ModelMappings.USER_LOGGED))
                    && !SecurityUtil.isAuthenticated()) {
                String fiscalcode = (String) attributes.get("fiscalcode");
                String username = (String) attributes.get("username");

                _log.debug(_method + " - Codice Fiscale ricevuto: " + fiscalcode);
                _log.debug(_method + " - Username ricevuto: " + username);

                if (null == (fiscalcode)) {
                    this.message = "Attributo Codice Fiscale non trovato nella mappa degli attributi.";
                    _log.warn(_method + " - " + this.message);
                    return false;
                }
                String _key = (String) attributes.get("keyrequest");

                LoginUserDetailsService loginUserDetailsService
                        = (LoginUserDetailsService) SpringApplicationContext.getBean(LoginUserDetailsService.class);

                try {
                    loginUserDetailsService.loadUserByUsername(fiscalcode);
                } catch (UsernameNotFoundException e) {

                    User _user = new User();
                    _user.setUsername(fiscalcode);
                    _user.setPassword(SecurityUtil.md5PasswordEncoder(fiscalcode));
                    _user.setFiscalcode(fiscalcode);
                    _user.setEmail((String) attributes.get("email"));
                    _user.setFirstname((String) attributes.get("firstname"));
                    _user.setLastname((String) attributes.get("lastname"));
                    _user.setEnabled(true);
                    _user = loginUserDetailsService.createNewUser(_user);
                    _log.info("Ho inserito l'utente nel db: " + _user.getFiscalcode());
                    UserRole _ur = new UserRole(_user, "ROLE_USER");
                    loginUserDetailsService.addUserRole(_ur);
                }
                loginUserDetailsService.setKeyLogin(_key, fiscalcode);
                response.sendRedirect(request.getContextPath() + "/ims?u=" + fiscalcode + "&key=" + _key);
            } else {
                _log.debug(_method + " - Ho trovato l'utente in sessione.");
            }
        } catch (Exception ex) {
            String _msg = "Non e' possibile accedere in questo momento. Si prega di riprovare piu' tardi.";
            _log.error(_method + " - " + _msg, ex);
            ex.printStackTrace(System.out);
            esito = false;
            this.message = _msg;
        } finally {
            _log.debug(_method + " - end");
        }

        request.setAttribute("LOGIN", "login");
        return esito;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
