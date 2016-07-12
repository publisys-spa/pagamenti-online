package it.publisys.pagamentionline.service.login;

import it.publisys.pagamentionline.domain.user.User;
import it.publisys.pagamentionline.domain.user.UserRole;
import it.publisys.pagamentionline.repository.UserRepository;
import it.publisys.pagamentionline.repository.UserRoleRepository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 *
 * @author mcolucci
 */
public class LoginUserDetailsService
    implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;

    @Override
    public User loadUserByUsername(String username)
        throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("Username non presente in archivio");
        }
        List<UserRole> _uRoles = userRoleRepository.findByUser(user);

        List<GrantedAuthority> listOfAuthorities = _uRoles.stream().map(ur -> new SimpleGrantedAuthority(ur.getAuthority())).collect(Collectors.toList());

        user.setAuthorities(listOfAuthorities);
        return user;
    }

    public int setKeyLogin(String key, String username) {
        User _user = loadUserByUsername(username);
        _user.setKeyRequest(key);
        userRepository.saveAndFlush(_user);
        return 1;
    }

    public User createNewUser(User user) {
        return userRepository.save(user);
    }

    public void addUserRole(UserRole userRole) {
        userRoleRepository.save(userRole);
    }

}
