package it.publisys.pagamentionline.repository;

import it.publisys.pagamentionline.domain.impl.Applicazione;
import it.publisys.pagamentionline.domain.impl.Ente;
import it.publisys.pagamentionline.domain.impl.TipologiaTributo;
import it.publisys.pagamentionline.domain.impl.Tributo;
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
public interface TributoRepository
    extends JpaRepository<Tributo, Long> {

    List<Tributo> findByLogdDateIsNullOrderByDescrizione();

    List<Tributo> findByEnteAndLogdDateIsNullOrderByDescrizione(Ente e);

    List<Tributo> findByEnteAndTipologiaTributoAndLogdDateIsNullOrderByDescrizione(Ente e, TipologiaTributo tipologiaTributo);

    Tributo  findByEnteAndTipologiaTributoAndCodIntegrazioneAndLogdDateIsNull(Ente e, TipologiaTributo tipologiaTributo,String codIntegrazione );

    List<Tributo> findAllByApplicazione(Applicazione applicazione);

    Tributo findByCodice(String codice);

    Page<Tributo> findByLogdDateIsNullOrderByDescrizione(Pageable pageable);

    Tributo findByTipologiaTributo(TipologiaTributo tipologiaTributo);
}
