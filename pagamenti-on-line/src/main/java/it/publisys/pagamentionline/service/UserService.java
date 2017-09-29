package it.publisys.pagamentionline.service;

import it.publisys.pagamentionline.domain.impl.Provider;
import it.publisys.pagamentionline.domain.impl.Tributo;
import it.publisys.pagamentionline.domain.user.User;
import it.publisys.pagamentionline.domain.user.UserRole;
import it.publisys.pagamentionline.repository.UserRepository;
import it.publisys.pagamentionline.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author mcolucci
 */
@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    public User getUser(Long id) {
        return userRepository.findOne(id);
    }

    public User getUser(String username) {
        return userRepository.findByUsername(username);
    }

    public User getUserByFiscalcode(String codiceFiscale) {
        return userRepository.findByFiscalcode(codiceFiscale);
    }

    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public List<UserRole> getRoles(User user) {
        return userRoleRepository.findByUser(user);
    }

    public User delete(Long id, String username, Long idRuolo) {
        User userMod = this.getUser(id);
        userMod.setLogdDate(new Date());
        userMod.setLogdUser(username);
        UserRole role = userRoleRepository.findOne(idRuolo);
        userRoleRepository.delete(role);
        return userRepository.saveAndFlush(userMod);
    }

    public UserRole getUserRole(Long id) {
        return userRoleRepository.findOne(id);
    }

    public UserRole saveRole(UserRole role) {
        User userMod = this.getUser(role.getUser().getId());
        userMod.setLogdDate(new Date());
        role.setUser(userMod);
        userRepository.saveAndFlush(userMod);
        return userRoleRepository.save(role);
    }

}
