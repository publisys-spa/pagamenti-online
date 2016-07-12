package it.publisys.pagamentionline.handler;

import it.publisys.pagamentionline.ModelMappings;
import it.publisys.pagamentionline.domain.user.User;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.SavedRequest;

/**
 *
 * @author mcolucci
 */
public class LoginAuthenticationSuccessHandler
    extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication auth)
        throws IOException, ServletException {
        HttpSession session = request.getSession(false);

        String j_key = request.getParameter("j_key");

        User user = (User) auth.getPrincipal();

        if (j_key != null && j_key.equals(user.getKeyRequest())) {
            session.setAttribute(ModelMappings.USER_LOGGED, user);
        } else {
            session.setAttribute(ModelMappings.USER_LOGGED, user);
        }

        // changeLastLoginTime(username)
        //userService.changeLastLoginTime(auth.getName());
        SavedRequest savedReq = (SavedRequest) session.getAttribute("SPRING_SECURITY_SAVED_REQUEST");
        if (savedReq != null) {
            response.sendRedirect(savedReq.getRedirectUrl());
        }

        super.onAuthenticationSuccess(request, response, auth);
    }

}
