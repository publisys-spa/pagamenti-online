package it.publisys.pagamentionline.repository;

import it.publisys.pagamentionline.domain.impl.Ente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Francesco A. Tabino
 */
@Transactional
public interface EnteRepository extends JpaRepository<Ente, Long> {

    Page<Ente> findByLogdDateIsNullOrderByName(Pageable pageable);

    List<Ente> findByLogdDateIsNullOrderByName();

}
