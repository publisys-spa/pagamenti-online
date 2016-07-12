package it.publisys.pagamentionline.repository;

import it.publisys.pagamentionline.domain.impl.Pagamento;
import it.publisys.pagamentionline.domain.impl.Tributo;
import it.publisys.pagamentionline.domain.search.Filter;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mcolucci
 */
@Repository
public class SearchRepository {

    @PersistenceContext
    private EntityManager em;

    public List<Pagamento> searchPagamenti(Filter filter) {

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Pagamento> query = builder.createQuery(Pagamento.class);
        Root<Pagamento> pagamento = query.from(Pagamento.class);

        Join<Pagamento, Tributo> tributo = null;


        if (StringUtils.isNotBlank(filter.getTributo())) {
            tributo = pagamento.join("tributo");
        }

        List<Predicate> _predicates = new ArrayList<>();

        if (StringUtils.isNotBlank(filter.getPid())) {
            _predicates.add(builder.equal(pagamento.get("pid"), filter.getPid()));
        }
        if (StringUtils.isNotBlank(filter.getRefnumber())) {
            _predicates.add(builder.equal(pagamento.get("refnumber"), filter.getRefnumber()));
        }
        if (filter.getDataPagamento() != null) {
            _predicates.add(builder.equal(pagamento.get("dataPagamento"), filter.getDataPagamento()));
        }
        if (StringUtils.isNotBlank(filter.getCausale())) {
            _predicates.add(builder.like(pagamento.get("causale"), "%" + filter.getCausale() + "%"));
        }
        if (tributo != null) {
            if (StringUtils.isNotBlank(filter.getTributo())) {
                _predicates.add(builder.equal(tributo.get("id"), filter.getTributo()));
            }
        }

        if (!_predicates.isEmpty()) {
            query.where(builder.and(_predicates.toArray(new Predicate[_predicates.size()])));
        }

        return em.createQuery(query).getResultList();
    }

}
