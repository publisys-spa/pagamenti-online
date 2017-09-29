package it.publisys.pagamentionline.repository;

import it.publisys.pagamentionline.domain.impl.Applicazione;
import it.publisys.pagamentionline.domain.impl.Pagamento;
import it.publisys.pagamentionline.domain.impl.Tributo;
import it.publisys.pagamentionline.domain.search.Filter;
import it.publisys.pagamentionline.domain.user.User;
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
        if (StringUtils.isNotBlank(filter.getIuv())) {
            _predicates.add(builder.equal(pagamento.get("refnumber"), filter.getIuv()));
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

    public List<User> searchUsers(Filter filter) {

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<User> query = builder.createQuery(User.class);
        Root<User> user = query.from(User.class);

        List<Predicate> _predicates = new ArrayList<>();

        if (StringUtils.isNotBlank(filter.getCodiceFiscale())) {
            _predicates.add(builder.equal(user.get("fiscalcode"), filter.getCodiceFiscale()));
        }
        if (StringUtils.isNotBlank(filter.getNome())) {
            _predicates.add(builder.like(user.get("firstname"),  "%" +filter.getNome()+ "%"));
        }
        if (filter.getDataPagamento() != null) {
            _predicates.add(builder.like(user.get("lastname"), "%" + filter.getCognome()+ "%"));
        }
        if (!_predicates.isEmpty()) {
            query.where(builder.and(_predicates.toArray(new Predicate[_predicates.size()])));
        }
        return em.createQuery(query).getResultList();
    }

    public List<Applicazione> searchApplicazioni(Filter filter) {

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Applicazione> query = builder.createQuery(Applicazione.class);
        Root<Applicazione> app = query.from(Applicazione.class);

        List<Predicate> _predicates = new ArrayList<>();

        if (StringUtils.isNotBlank(filter.getCodiceFiscale())) {
            _predicates.add(builder.equal(app.get("codice"), filter.getCodice()));
        }
        if (StringUtils.isNotBlank(filter.getNome())) {
            _predicates.add(builder.like(app.get("descrizione"),  "%" +filter.getDescrizione()+ "%"));
        }
        if (filter.getDataPagamento() != null) {
            _predicates.add(builder.like(app.get("responsabile"), "%" + filter.getResponsabile()+ "%"));
        }
        if (!_predicates.isEmpty()) {
            query.where(builder.and(_predicates.toArray(new Predicate[_predicates.size()])));
        }
        return em.createQuery(query).getResultList();
    }


    public List<Tributo> searchTributi(Filter filter) {

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Tributo> query = builder.createQuery(Tributo.class);
        Root<Tributo> tributo = query.from(Tributo.class);

        Join<Tributo, Applicazione> applicazione = null;


        if (StringUtils.isNotBlank(filter.getCodice())) {
            applicazione = tributo.join("applicazione");
        }

        List<Predicate> _predicates = new ArrayList<>();


        if (StringUtils.isNotBlank(filter.getNome())) {
            _predicates.add(builder.like(tributo.get("nome"), "%" + filter.getNome() + "%"));
        }
        if (tributo != null) {
            if (StringUtils.isNotBlank(filter.getCodice())) {
                _predicates.add(builder.equal(applicazione.get("codice"), filter.getCodice()));
            }
        }

        if (!_predicates.isEmpty()) {
            query.where(builder.and(_predicates.toArray(new Predicate[_predicates.size()])));
        }

        return em.createQuery(query).getResultList();
    }




}
