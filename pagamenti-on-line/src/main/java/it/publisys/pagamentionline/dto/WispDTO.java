package it.publisys.pagamentionline.dto;

/**
 * @author Francesco A. Tabino
 */
public class WispDTO {

    private String idDominio;
    private String keyPA;
    private String urlBack;
    private String urlReturn;

    private String enteCreditore;
    private String primitiva;
    private String numPagamentiRPT;
    private String stornoPagamento;
    private String bolloDigitale;
    private String terzoModelloPagamento;

    public String getIdDominio() {
        return idDominio;
    }

    public void setIdDominio(String idDominio) {
        this.idDominio = idDominio;
    }

    public String getKeyPA() {
        return keyPA;
    }

    public void setKeyPA(String keyPA) {
        this.keyPA = keyPA;
    }

    public String getUrlBack() {
        return urlBack;
    }

    public void setUrlBack(String urlBack) {
        this.urlBack = urlBack;
    }

    public String getUrlReturn() {
        return urlReturn;
    }

    public void setUrlReturn(String urlReturn) {
        this.urlReturn = urlReturn;
    }

    public String getEnteCreditore() {
        return enteCreditore;
    }

    public void setEnteCreditore(String enteCreditore) {
        this.enteCreditore = enteCreditore;
    }

    public String getPrimitiva() {
        return primitiva;
    }

    public void setPrimitiva(String primitiva) {
        this.primitiva = primitiva;
    }

    public String getNumPagamentiRPT() {
        return numPagamentiRPT;
    }

    public void setNumPagamentiRPT(String numPagamentiRPT) {
        this.numPagamentiRPT = numPagamentiRPT;
    }

    public String getStornoPagamento() {
        return stornoPagamento;
    }

    public void setStornoPagamento(String stornoPagamento) {
        this.stornoPagamento = stornoPagamento;
    }

    public String getBolloDigitale() {
        return bolloDigitale;
    }

    public void setBolloDigitale(String bolloDigitale) {
        this.bolloDigitale = bolloDigitale;
    }

    public String getTerzoModelloPagamento() {
        return terzoModelloPagamento;
    }

    public void setTerzoModelloPagamento(String terzoModelloPagamento) {
        this.terzoModelloPagamento = terzoModelloPagamento;
    }
}
