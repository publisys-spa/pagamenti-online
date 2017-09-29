package it.publisys.pagamentionline.repository;

import it.publisys.pagamentionline.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author mcolucci
 */
@Transactional
public interface UserRepository
        extends JpaRepository<User, Long> {

    User findByFiscalcode(String codiceFiscale);

    User findByUsername(String username);

    User findByUsernameAndKeyRequest(String username, String keyRequest);

    User findByEmail(String email);

}
