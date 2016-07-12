package it.publisys.pagamentionline.repository;

import it.publisys.pagamentionline.domain.enumeration.MailStatusEnum;
import it.publisys.pagamentionline.domain.user.Mail;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author mcolucci
 */
@Transactional
public interface MailRepository
    extends JpaRepository<Mail, Long> {

    List<Mail> findByStatus(MailStatusEnum status);
    
    
}
