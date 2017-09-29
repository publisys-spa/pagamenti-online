package it.publisys.pagamentionline.repository;

import it.publisys.pagamentionline.PagamentiOnlineKey;
import it.publisys.pagamentionline.domain.impl.Pagamento;
import it.publisys.pagamentionline.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author vasta
 */
@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

    Optional<Pagamento> findByPid(String pid);

    Optional<Pagamento> findByCodVersamentoEnte(String codVersamentoEnte);

    Page<Pagamento> findByEsecutoreAndStatoPagamentoNotAndLogdDateIsNullOrderByDataPagamentoDesc(Pageable pageable, User esecutore, String stato);

    List<Pagamento> findFirst5ByEsecutoreAndStatoPagamentoAndLogdDateIsNullOrderByDataPagamentoDesc(User esecutore, String statoPagamento);

    List<Pagamento> findByEsecutoreAndStatoPagamentoNotAndLogdDateIsNullAndPidOrderByDataPagamentoDesc(User user, String statoPagamento, String pid );

    List<Pagamento> findByEsecutoreAndStatoPagamentoNotAndLogdDateIsNullAndIuvOrderByDataPagamentoDesc(User user, String statoPagamento, String iuv );

}
