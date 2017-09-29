package it.publisys.pagamentionline.transformer;

import it.govpay.servizi.v2_3.gpprt.GpChiediListaVersamentiResponse;
import it.publisys.pagamentionline.dto.DebitoDTO;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;

/**
 * @author Francesco A. Tabino
 */
@Component
public class VersamentoTransformer {

    public DebitoDTO transofrmToPagamento(GpChiediListaVersamentiResponse.Versamento versamento, String codFiscale) {
        DebitoDTO _dto = new DebitoDTO();
        _dto.setCausale(versamento.getCausale());
        _dto.setImportoDovuto(versamento.getImportoTotale().setScale(2, BigDecimal.ROUND_UNNECESSARY));
        _dto.setDataScadenza(versamento.getDataScadenza());
        _dto.setStato(versamento.getStato().value());

        _dto.setCodApplicazione(versamento.getCodApplicazione());
        _dto.setIuv(versamento.getIuv());
        _dto.setCodDominio(versamento.getCodDominio());
        _dto.setCodVersamentoEnte(versamento.getCodVersamentoEnte());
        _dto.setCodFiscale(codFiscale);
        try {
            if(null != versamento.getIuv()) {
                _dto.setBarCode(new String(versamento.getBarCode(), "UTF-8"));
                _dto.setQrCode(new String(versamento.getQrCode(), "UTF-8"));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return _dto;
    }


}
