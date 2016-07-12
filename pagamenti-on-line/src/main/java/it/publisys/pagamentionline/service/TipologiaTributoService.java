package it.publisys.pagamentionline.service;

import it.publisys.pagamentionline.domain.impl.TipologiaTributo;
import it.publisys.pagamentionline.repository.TipologiaTributoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author Vasta, mcolucci
 */
@Service
@Transactional
public class TipologiaTributoService {

    @Autowired
    private TipologiaTributoRepository tipologiaTributoRepository;

    public TipologiaTributo getTipologia(Long id) {
        return tipologiaTributoRepository.findOne(id);
    }

    public List<TipologiaTributo> getAllTipologie() {
        return tipologiaTributoRepository.findByLogdDateIsNull();
    }

    public Page<TipologiaTributo> getAllTipologie(Pageable pageable) {
        return tipologiaTributoRepository.findByLogdDateIsNull(pageable);
    }

    public TipologiaTributo delete(Long id, String username) {

        TipologiaTributo tipologiaMod = this.getTipologia(id);
        tipologiaMod.setLogdDate(new Date());
        tipologiaMod.setLogdUser(username);

        return tipologiaTributoRepository.saveAndFlush(tipologiaMod);

    }

    public TipologiaTributo save(TipologiaTributo tipologia, String username) {
        if (tipologia.getId() != null) {
            TipologiaTributo tipologiaMod = this.getTipologia(tipologia.getId());
            tipologiaMod.setNome(tipologia.getNome());
            tipologiaMod.setDescrizione(tipologia.getDescrizione());
            tipologiaMod.setCodiceRadice(tipologia.getCodiceRadice());
            tipologiaMod.setTipo(tipologia.getTipo());
            tipologiaMod.setLoguUser(username);
            tipologiaMod.setLoguDate(new Date());
            return tipologiaTributoRepository.saveAndFlush(tipologiaMod);
        } else {
            tipologia.setLogcUser(username);
            tipologia.setLogcDate(new Date());
            return tipologiaTributoRepository.saveAndFlush(tipologia);
        }
    }

}
