package it.publisys.pagamentionline.service;

import it.publisys.pagamentionline.domain.user.User;
import it.publisys.pagamentionline.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author mcolucci
 */
@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User getUser(Long id) {
        return userRepository.findOne(id);
    }

    public User getUser(String username) {
        return userRepository.findByUsername(username);
    }

}
