package it.publisys.pagamentionline.service;

import it.publisys.pagamentionline.domain.impl.Applicazione;
import it.publisys.pagamentionline.domain.impl.TipologiaTributo;
import it.publisys.pagamentionline.repository.ApplicazioneRepository;
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
public class ApplicazioneService {

    @Autowired
    private ApplicazioneRepository applicazioneRepository;

    public Applicazione getApplicazione(Long id) {
        return applicazioneRepository.findOne(id);
    }


    public List<Applicazione> getAllApplicazioni() {
        return applicazioneRepository.findByLogdDateIsNullOrderByCodice();
    }

    public Page<Applicazione> getAllApplicazioni(Pageable pageable) {
        return applicazioneRepository.findByLogdDateIsNullOrderByCodice(pageable);
    }

    public Applicazione delete(Long id, String username) {
        Applicazione app = this.getApplicazione(id);
        app.setLogdDate(new Date());
        app.setLogdUser(username);
        return applicazioneRepository.saveAndFlush(app);

    }

    public Applicazione save(Applicazione app, String username) {
        if (app.getId() != null) {
            Applicazione appMod = this.getApplicazione(app.getId());
            appMod.setCodice(app.getCodice());
            appMod.setDescrizione(app.getDescrizione());
            appMod.setResponsabile(app.getResponsabile());
            appMod.setLoguUser(username);
            appMod.setLoguDate(new Date());
            return applicazioneRepository.saveAndFlush(appMod);
        } else {
            app.setLogcUser(username);
            app.setLogcDate(new Date());
            return applicazioneRepository.saveAndFlush(app);
        }
    }

}
