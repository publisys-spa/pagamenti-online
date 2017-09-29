package it.publisys.pagamentionline.service;

import it.publisys.pagamentionline.domain.impl.Applicazione;
import it.publisys.pagamentionline.domain.impl.Pagamento;
import it.publisys.pagamentionline.domain.impl.Tributo;
import it.publisys.pagamentionline.domain.search.Filter;
import it.publisys.pagamentionline.domain.user.User;
import it.publisys.pagamentionline.repository.SearchRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author mcolucci, acoviello
 */
@Service
@Transactional
public class SearchService {

    @Autowired
    private SearchRepository searchRepository;

    @Autowired
    private TributoService tributoService;

    @Autowired
    private ApplicazioneService applicazioneService;

    @Autowired
    private EnteService enteService;

    public TributoService getTributoService() {
        return tributoService;
    }

    public ApplicazioneService getApplicazioneService() {
        return applicazioneService;
    }

    public List<Pagamento> searchPagamenti(Filter filter) {
        return searchRepository.searchPagamenti(filter);
    }

    public List<User> searchUsers(Filter filter) {
        return searchRepository.searchUsers(filter);
    }

    public List<Applicazione> searchApplicazioni(Filter filter) {
        return searchRepository.searchApplicazioni(filter);
    }

    public List<Tributo> searchTributi(Filter filter) {
        return searchRepository.searchTributi(filter);
    }


    public EnteService getEnteService() {
        return enteService;
    }
}
