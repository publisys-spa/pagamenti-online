package it.publisys.pagamentionline.util.sec;


import it.publisys.pagamentionline.domain.user.User;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 *
 * @author mcolucci
 */
public class SecurityUtil {

    protected static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static boolean isAnonymous() {
        return getAuthentication() instanceof AnonymousAuthenticationToken;
    }

    public static boolean isAuthenticated() {
        try {
            return !isAnonymous() && getAuthentication().isAuthenticated();
        } catch (Exception e) {
            return false;
        }
    }

    public static User getPrincipal() {
        if (isAuthenticated()) {
            return (User) getAuthentication().getPrincipal();
        } else {
            return null;
        }
    }

    public static String getPrincipalName() {
        if (isAuthenticated()) {
            User _principal = (User) getAuthentication().getPrincipal();
            return _principal.getUsername();
        } else {
            return "guest";
        }
    }

    public static String md5PasswordEncoder(String val) {
        Md5PasswordEncoder encoder = new Md5PasswordEncoder();
        return encoder.encodePassword(val, null);
    }

}
