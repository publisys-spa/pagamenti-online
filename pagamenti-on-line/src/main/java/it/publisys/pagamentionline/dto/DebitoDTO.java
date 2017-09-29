package it.publisys.pagamentionline.dto;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Francesco A. Tabino
 */
public class DebitoDTO {

    private Date dataScadenza;
    private String causale;
    private BigDecimal importoDovuto;
    private String stato;
    private String codVersamentoEnte;
    private String codApplicazione;
    private String iuv;
    private String qrCode;
    private String barCode;
    private String codDominio;
    private String codFiscale;

    public Date getDataScadenza() {
        return dataScadenza;
    }

    public void setDataScadenza(Date dataScadenza) {
        this.dataScadenza = dataScadenza;
    }

    public String getCausale() {
        return causale;
    }

    public void setCausale(String causale) {
        this.causale = causale;
    }

    public BigDecimal getImportoDovuto() {
        return importoDovuto;
    }

    public void setImportoDovuto(BigDecimal importoDovuto) {
        this.importoDovuto = importoDovuto;
    }

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }

    public String getCodVersamentoEnte() {
        return codVersamentoEnte;
    }

    public void setCodVersamentoEnte(String codVersamentoEnte) {
        this.codVersamentoEnte = codVersamentoEnte;
    }

    public String getCodApplicazione() {
        return codApplicazione;
    }

    public void setCodApplicazione(String codApplicazione) {
        this.codApplicazione = codApplicazione;
    }

    public String getIuv() {
        return iuv;
    }

    public void setIuv(String iuv) {
        this.iuv = iuv;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getCodDominio() {
        return codDominio;
    }

    public void setCodDominio(String codDominio) {
        this.codDominio = codDominio;
    }

    public String getCodFiscale() {
        return codFiscale;
    }

    public void setCodFiscale(String codFiscale) {
        this.codFiscale = codFiscale;
    }
}
