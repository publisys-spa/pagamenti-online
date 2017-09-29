package it.publisys.pagamentionline.repository;

import it.publisys.pagamentionline.domain.impl.TipologiaTributo;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author vasta
 */
@Transactional
public interface TipologiaTributoRepository
    extends JpaRepository<TipologiaTributo, Long> {

    List<TipologiaTributo> findByLogdDateIsNullOrderByDescrizione();

    Page<TipologiaTributo> findByLogdDateIsNullOrderByDescrizione(Pageable pageable);

    TipologiaTributo findByCodiceRadice(String codiceRadice);
}
