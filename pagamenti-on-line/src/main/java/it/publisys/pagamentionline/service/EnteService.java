package it.publisys.pagamentionline.service;

import it.publisys.pagamentionline.domain.impl.Ente;
import it.publisys.pagamentionline.repository.EnteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author Francesco A. Tabino
 */
@Service
public class EnteService {

    @Autowired
    private EnteRepository enteRepository;

    public Page<Ente> findAll(Pageable pageable) {
        return enteRepository.findByLogdDateIsNullOrderByName(pageable);
    }

    public List<Ente> findAll() {
        return enteRepository.findByLogdDateIsNullOrderByName();
    }

    public Ente getOne(Long id) {
        return enteRepository.findOne(id);
    }

    public boolean exists(Long id) {
        return enteRepository.exists(id);
    }

    public Ente delete(Long id, String username) {

        Ente ente = this.getOne(id);
        ente.setLogdDate(new Date());
        ente.setLogdUser(username);

        return enteRepository.saveAndFlush(ente);

    }

    public Ente save(Ente ente, String username) {
        if (ente.getId() != null) {
            Ente one = this.getOne(ente.getId());
            one.setName(ente.getName());
            one.setFiscalCode(ente.getFiscalCode());
            one.setTributi(ente.getTributi());
            one.setLoguUser(username);
            one.setLoguDate(new Date());
            return enteRepository.saveAndFlush(one);
        } else {
            ente.setLogcUser(username);
            ente.setLogcDate(new Date());
            return enteRepository.saveAndFlush(ente);
        }
    }

}
