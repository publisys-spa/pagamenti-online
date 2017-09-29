package it.publisys.pagamentionline.util.sec;

import java.util.Collection;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 *
 * @author mcolucci
 */
public class AuthorityUtil extends SecurityUtil {

    public static final String ROLE_GUEST = "ROLE_GUEST";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_DIR= "ROLE_DIR";

    public static Collection<? extends GrantedAuthority> getAuthorities() {
        return getAuthentication().getAuthorities();
    }

    public static boolean isAdminLogged() {
        return hasRole(ROLE_ADMIN);
    }

    public static boolean isDirLogged() {
        return hasRole(ROLE_DIR);
    }

    public static boolean isUserLogged() {
        return hasRole(ROLE_USER);
    }

    public static boolean isGuestLogged() {
        return hasRole(ROLE_GUEST);
    }

    private static boolean hasRole(String role) {
        HttpServletRequest _request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        SecurityContextHolderAwareRequestWrapper _sc = new SecurityContextHolderAwareRequestWrapper(_request, "");
        return _sc.isUserInRole(role);
    }
}
