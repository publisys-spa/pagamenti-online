package it.publisys.pagamentionline.service;

import it.publisys.pagamentionline.domain.impl.Applicazione;
import it.publisys.pagamentionline.domain.impl.Ente;
import it.publisys.pagamentionline.domain.impl.TipologiaTributo;
import it.publisys.pagamentionline.domain.impl.Tributo;
import it.publisys.pagamentionline.repository.TributoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author vasta, mcolucci
 */
@Service
@Transactional
public class TributoService {

    @Autowired
    private TributoRepository tributoRepository;
    @Autowired
    private TipologiaTributoService tipologiaTributoService;
    @Autowired
    private ApplicazioneService applicazioneService;

    @Autowired
    private EnteService enteService;

    public TipologiaTributoService getTipologiaTributoService() {
        return tipologiaTributoService;
    }

    public Tributo getTributo(Long id) {
        return tributoRepository.findOne(id);
    }

    public Tributo getTributiTipologia(TipologiaTributo tipologiaTributo) {
        return tributoRepository.findByTipologiaTributo(tipologiaTributo);
    }
    public Tributo  getByEnteTipologiaTributoCodIntegrazione(Ente e, TipologiaTributo tipologiaTributo,String codIntegrazione ){
        return tributoRepository.findByEnteAndTipologiaTributoAndCodIntegrazioneAndLogdDateIsNull(e, tipologiaTributo, codIntegrazione);
    }
    public List<Tributo> getAllTributi() {
        return tributoRepository.findByLogdDateIsNullOrderByDescrizione();
    }

    public List<Tributo> findAllByEnte(Ente e) {
        return tributoRepository.findByEnteAndLogdDateIsNullOrderByDescrizione(e);
    }

    public List<Tributo> findAllByEnteTipologia(Ente e, TipologiaTributo tipologiaTributo) {
        return tributoRepository.findByEnteAndTipologiaTributoAndLogdDateIsNullOrderByDescrizione(e, tipologiaTributo);
    }

    public List<Tributo> findAllByApplicazione(Applicazione applicazione) {
        return tributoRepository.findAllByApplicazione(applicazione);
    }
    public Tributo findByCodice(String codice) {
        return tributoRepository.findByCodice(codice);
    }

    public Page<Tributo> getAllTributi(Pageable pageable) {
        return tributoRepository.findByLogdDateIsNullOrderByDescrizione(pageable);
    }

    public Tributo delete(Long id, String username) {
        Tributo tributoMod = this.getTributo(id);
        tributoMod.setLogdDate(new Date());
        tributoMod.setLogdUser(username);
        return tributoRepository.saveAndFlush(tributoMod);
    }

    public Tributo save(Tributo tributo, String username) {
        Ente ente = enteService.getOne(tributo.getEnte().getId());
        if (tributo.getId() != null) {
            Tributo tributoMod = this.getTributo(tributo.getId());
            tributoMod.setNome(tributo.getNome());
            tributoMod.setDescrizione(tributo.getDescrizione());
            tributoMod.setCodIntegrazione(tributo.getCodIntegrazione());
            tributoMod.setAllegati(tributo.getAllegati());
            if (tributo.getTipologiaTributo() != null) {
                tributoMod.setTipologiaTributo(getTipologiaTributoService().getTipologia(tributo.getTipologiaTributo().getId()));
            }
            if (tributo.getApplicazione() != null) {
                tributoMod.setApplicazione(applicazioneService.getApplicazione(tributo.getApplicazione().getId()));
            }
            tributoMod.setCodice(tributo.getCodice());
            tributoMod.setAnno(tributo.getAnno());
            tributoMod.setEnte(ente);
            tributoMod.setLoguUser(username);
            tributoMod.setLoguDate(new Date());
            return tributoRepository.saveAndFlush(tributoMod);
        } else {
            if (tributo.getTipologiaTributo() != null) {
                tributo.setTipologiaTributo(getTipologiaTributoService().getTipologia(tributo.getTipologiaTributo().getId()));
            }
            if (tributo.getApplicazione() != null) {
                tributo.setApplicazione(applicazioneService.getApplicazione(tributo.getApplicazione().getId()));
            }
            tributo.setEnte(ente);
            tributo.setLogcUser(username);
            tributo.setLogcDate(new Date());
            return tributoRepository.saveAndFlush(tributo);
        }
    }

}
