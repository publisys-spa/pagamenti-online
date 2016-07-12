package it.publisys.pagamentionline.service;

import it.publisys.pagamentionline.domain.impl.Rata;
import it.publisys.pagamentionline.domain.impl.Tributo;
import it.publisys.pagamentionline.repository.RataRepository;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author vasta, mcolucci
 */
@Service
@Transactional
public class RataService {

    @Autowired
    private RataRepository rataRepository;
    @Autowired
    private TributoService tributoService;

    public TributoService getTributoService() {
        return tributoService;
    }

    public Rata getRata(Long id) {
        return rataRepository.findOne(id);
    }

    public Optional<Rata> findByCustomId(String customId) {
        return rataRepository.findByCustomId(customId);
    }

    public List<Rata> getAllRata(Tributo tributo) {
        return rataRepository.findAllRataByTributoAndLogdDateIsNull(tributo);
    }

    public Rata delete(Long id, String username) {
        Rata rataMod = this.getRata(id);
        rataMod.setLogdDate(new Date());
        rataMod.setLogdUser(username);

        return rataRepository.saveAndFlush(rataMod);
    }

    public Rata save(Rata rata, String username) {
        if (rata.getId() != null) {
            Rata rataMod = this.getRata(rata.getId());
            rataMod.setNome(rata.getNome());
            rataMod.setDescrizione(rata.getDescrizione());
            rataMod.setDataDa(rata.getDataDa());
            rataMod.setDataA(rata.getDataA());
            rataMod.setCausale(rata.getCausale());
            rataMod.setCustomId(rata.getCustomId());

            if (rata.getTributo() != null) {
                rataMod.setTributo(getTributoService().getTributo(rata.getTributo().getId()));
            }

            rataMod.setLoguUser(username);
            rataMod.setLoguDate(new Date());

            return rataRepository.saveAndFlush(rataMod);
        } else {

            if (rata.getTributo() != null) {
                rata.setTributo(getTributoService().getTributo(rata.getTributo().getId()));
            }

            rata.setLogcUser(username);
            rata.setLogcDate(new Date());
            return rataRepository.saveAndFlush(rata);
        }
    }

}
