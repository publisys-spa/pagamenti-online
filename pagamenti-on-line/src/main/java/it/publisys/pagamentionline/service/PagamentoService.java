package it.publisys.pagamentionline.service;

import it.publisys.pagamentionline.PagamentiOnlineKey;
import it.publisys.pagamentionline.domain.impl.Ente;
import it.publisys.pagamentionline.domain.impl.Pagamento;
import it.publisys.pagamentionline.domain.impl.Provider;
import it.publisys.pagamentionline.domain.user.User;
import it.publisys.pagamentionline.repository.PagamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author mcolucci
 */
@Service
@Transactional
public class PagamentoService {


    @Autowired
    private PagamentoRepository pagamentoRepository;
    @Autowired
    private TributoService tributoService;
    @Autowired
    private RataService rataService;

    @Autowired
    private UserService userService;
    @Autowired
    private EnteService enteService;

    public List<Pagamento> getAll() {
        return pagamentoRepository.findAll();
    }

    public Page<Pagamento> getAll(Pageable pageable) {
        return pagamentoRepository.findAll(pageable);
    }

    public Pagamento getPagamento(Long id) {
        return pagamentoRepository.findOne(id);
    }

    public Optional<Pagamento> findByPid(String pid) {
        return pagamentoRepository.findByPid(pid);
    }

    public Optional<Pagamento> findByCodVersamentoEnte(String codVersamentoEnte) {
        return pagamentoRepository.findByCodVersamentoEnte(codVersamentoEnte);
    }

    public Page<Pagamento> findAllByUser(Pageable pageable, User user) {
        return pagamentoRepository.findByEsecutoreAndStatoPagamentoNotAndLogdDateIsNullOrderByDataPagamentoDesc(pageable, user, PagamentiOnlineKey.STATO_ELIMINATO);
    }

    public List<Pagamento> findAllByUserPid(User user, String pid) {
        return pagamentoRepository.findByEsecutoreAndStatoPagamentoNotAndLogdDateIsNullAndPidOrderByDataPagamentoDesc(user, PagamentiOnlineKey.STATO_ELIMINATO, pid);
    }

    public List<Pagamento> findAllByUserIuv(User user, String iuv) {
        return pagamentoRepository.findByEsecutoreAndStatoPagamentoNotAndLogdDateIsNullAndIuvOrderByDataPagamentoDesc(user, PagamentiOnlineKey.STATO_ELIMINATO, iuv);
    }

    public List<Pagamento> findFisrt5ByUser(User user) {
        return pagamentoRepository.findFirst5ByEsecutoreAndStatoPagamentoAndLogdDateIsNullOrderByDataPagamentoDesc(user, PagamentiOnlineKey.STATO_ESEGUITO);
    }

    public Pagamento save(Pagamento pagamento, String username) {

        if (pagamento.getId() != null) {
            Pagamento _pagMod = this.getPagamento(pagamento.getId());

            if (pagamento.getEnte() != null) {
                _pagMod.setEnte(enteService.getOne(pagamento.getEnte().getId()));
            }
            if (pagamento.getTributo() != null) {
                _pagMod.setTributo(tributoService.getTributo(pagamento.getTributo().getId()));
            }
            if (pagamento.getRata() != null) {
                _pagMod.setRata(rataService.getRata(pagamento.getRata().getId()));
            }
            if (pagamento.getEsecutore() != null) {
                _pagMod.setEsecutore(userService.getUser(pagamento.getEsecutore().getId()));
            }

            _pagMod.setTipologia(pagamento.getTipologia());
            _pagMod.setImporto(pagamento.getImporto());
            _pagMod.setImportoCommissione(pagamento.getImportoCommissione());
            _pagMod.setCausale(pagamento.getCausale());
            _pagMod.setAttoAccertamento(pagamento.getAttoAccertamento());
            if (pagamento.getIuv() != null) {
                _pagMod.setIuv(pagamento.getIuv());
            }
            if (pagamento.getIdSessione() != null) {
                _pagMod.setIdSessione(pagamento.getIdSessione());
            }
            _pagMod.setCcp(pagamento.getCcp());
            _pagMod.setDateProcessed(pagamento.getDateProcessed());
            _pagMod.setStatusResponse(pagamento.getStatusResponse());
            _pagMod.setKeyWisp(pagamento.getKeyWisp());
            _pagMod.setStatoPagamento(pagamento.getStatoPagamento());
            if (pagamento.getIur() != null) {
                _pagMod.setIur(pagamento.getIur());
            }
            _pagMod.setCodVersamentoEnte(pagamento.getCodVersamentoEnte());
            if(pagamento.getCodPsp() != null) {
                _pagMod.setCodPsp(pagamento.getCodPsp());
            }
            _pagMod.setLoguUser(username);
            _pagMod.setLoguDate(new Date());
            return pagamentoRepository.saveAndFlush(_pagMod);
        } else {
            if (pagamento.getEnte() != null) {
                pagamento.setEnte(enteService.getOne(pagamento.getEnte().getId()));
            }
            if (pagamento.getTributo() != null) {
                pagamento.setTributo(tributoService.getTributo(pagamento.getTributo().getId()));
            }
            if (pagamento.getRata() != null) {
                pagamento.setRata(rataService.getRata(pagamento.getRata().getId()));
            }
            if (pagamento.getEsecutore() != null) {
                pagamento.setEsecutore(userService.getUser(pagamento.getEsecutore().getId()));
            }

            pagamento.setPid("P" + UUID.randomUUID().toString().replaceAll("-", ""));
            if (null == pagamento.getDataPagamento() ) {
                pagamento.setDataPagamento(new Date());
            }
            if (null == pagamento.getCodVersamentoEnte() || pagamento.getCodVersamentoEnte().isEmpty()) {
                pagamento.setCodVersamentoEnte(pagamento.getPid());
            }
            pagamento.setLogcUser(username);
            pagamento.setLogcDate(new Date());
            return pagamentoRepository.saveAndFlush(pagamento);
        }
    }

    public Properties loadProperties(Pagamento pagamento) {
        Ente ente = pagamento.getEnte();
        return loadProperties(ente);
    }

    public Properties loadProperties(Ente ente) {
        Provider provider = ente.getProvider();

        Properties prop = new Properties();
        try {
            prop.load(new ByteArrayInputStream(provider.getProperties().getBytes(StandardCharsets.UTF_8)));
        } catch (IOException ignored) {
        }
        return prop;
    }


}
