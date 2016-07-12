package it.publisys.pagamentionline.transformer;

import it.govpay.servizi.gpprt.GpChiediListaVersamentiResponse;
import it.publisys.pagamentionline.dto.DebitoDTO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @author Francesco A. Tabino
 */
@Component
public class VersamentoTransformer {

    public DebitoDTO transofrmToPagamento(GpChiediListaVersamentiResponse.Versamento versamento) {
        DebitoDTO _dto = new DebitoDTO();

        _dto.setCausale(versamento.getCausale());
        _dto.setImportoDovuto(versamento.getImportoTotale().setScale(2, BigDecimal.ROUND_UNNECESSARY));
        _dto.setDataScadenza(versamento.getDataScadenza());
        _dto.setStato(versamento.getStato().value());
        // TODO: 14/06/2016 vedere come prendere iuv
        _dto.setCodApplicazione(versamento.getCodApplicazione());
        _dto.setCodVersamentoEnte(versamento.getCodVersamentoEnte());

        return _dto;
    }


}
