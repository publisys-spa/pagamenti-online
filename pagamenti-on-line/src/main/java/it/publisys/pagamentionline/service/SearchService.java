package it.publisys.pagamentionline.service;

import it.publisys.pagamentionline.domain.impl.Pagamento;
import it.publisys.pagamentionline.domain.search.Filter;
import it.publisys.pagamentionline.repository.SearchRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author mcolucci
 */
@Service
@Transactional
public class SearchService {

    @Autowired
    private SearchRepository searchRepository;

    @Autowired
    private TributoService tributoService;


    @Autowired
    private PagamentoService pagamentoService;

    public TributoService getTributoService() {
        return tributoService;
    }

    public PagamentoService getPagamentoService() {
        return pagamentoService;
    }

    public List<Pagamento> searchPagamenti(Filter filter) {
        return searchRepository.searchPagamenti(filter);
    }


}
