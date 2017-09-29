package it.publisys.pagamentionline.repository;

import it.publisys.pagamentionline.domain.impl.Applicazione;
import it.publisys.pagamentionline.domain.impl.TipologiaTributo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *
 * @author acoviello
 */
@Transactional
public interface ApplicazioneRepository
    extends JpaRepository<Applicazione, Long> {

    List<Applicazione> findByLogdDateIsNullOrderByCodice();

    Page<Applicazione> findByLogdDateIsNullOrderByCodice(Pageable pageable);

}
