package it.publisys.pagamentionline.repository;

import it.publisys.pagamentionline.domain.user.User;
import it.publisys.pagamentionline.domain.user.UserRole;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author mcolucci
 */
@Transactional
public interface UserRoleRepository
    extends JpaRepository<UserRole, Long> {

    List<UserRole> findByUser(User user);

   UserRole findByUserAndAuthority(User user, String authority);
}
