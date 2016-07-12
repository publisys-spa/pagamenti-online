package it.publisys.pagamentionline.repository;

import it.publisys.pagamentionline.domain.impl.Rata;
import it.publisys.pagamentionline.domain.impl.Tributo;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author vasta
 */
@Transactional
public interface RataRepository
    extends JpaRepository<Rata, Long> {

    List<Rata> findAllRataByTributoAndLogdDateIsNull(Tributo tributo);

    Optional<Rata> findByCustomId(String customId);

}
