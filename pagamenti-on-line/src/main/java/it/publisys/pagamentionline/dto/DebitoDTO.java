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
}
