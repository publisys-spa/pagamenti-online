package it.publisys.pagamentionline.domain.rest;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author mcolucci
 * @version 1.0
 * @since <pre>16/09/15</pre>
 */
public class PayRest {

    private String pid;
    // rata
    @NotBlank
    private String rataCode;
    // beneficiario
    private String nome;
    private String cognome;
    @NotBlank
    private String codiceFiscale;
    @NotBlank
    private String email;
    //
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy", timezone = "Europe/Rome")
    @NotNull
    private Date dataPagamento;
    @NotBlank
    private String comune;
    @NotBlank
    private String causale;
    @NotNull
    private Double importo;
    @NotNull
    private Double importoCommissione;
    // dati BNL
    private String refnumber;
    private String dateProcessed;
    private String statusResponse;

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getRataCode() {
        return rataCode;
    }

    public void setRataCode(String rataCode) {
        this.rataCode = rataCode;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    public void setCodiceFiscale(String codiceFiscale) {
        this.codiceFiscale = codiceFiscale;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(Date dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public String getComune() {
        return comune;
    }

    public void setComune(String comune) {
        this.comune = comune;
    }

    public String getCausale() {
        return causale;
    }

    public void setCausale(String causale) {
        this.causale = causale;
    }

    public Double getImporto() {
        return importo;
    }

    public void setImporto(Double importo) {
        this.importo = importo;
    }

    public Double getImportoCommissione() {
        return importoCommissione;
    }

    public void setImportoCommissione(Double importoCommissione) {
        this.importoCommissione = importoCommissione;
    }

    public String getRefnumber() {
        return refnumber;
    }

    public void setRefnumber(String refnumber) {
        this.refnumber = refnumber;
    }

    public String getDateProcessed() {
        return dateProcessed;
    }

    public void setDateProcessed(String dateProcessed) {
        this.dateProcessed = dateProcessed;
    }

    public String getStatusResponse() {
        return statusResponse;
    }

    public void setStatusResponse(String statusResponse) {
        this.statusResponse = statusResponse;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
