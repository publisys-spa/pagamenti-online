package it.publisys.pagamentionline.domain.search;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Date;

/**
 * @author mcolucci
 */
public class Filter {

    private String tipo;
    //
    private String pid;
    private String refnumber;
    private Date dataPagamento;
    private String causale;
    //
    private String codiceFiscale;
    private String nome;
    private String cognome;
    //
    private String tributo;
    //
    private String targa;
    private String verbale;

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public Date getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(Date dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public String getCausale() {
        return causale;
    }

    public void setCausale(String causale) {
        this.causale = causale;
    }

    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    public void setCodiceFiscale(String codiceFiscale) {
        this.codiceFiscale = codiceFiscale;
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

    public String getTributo() {
        return tributo;
    }

    public void setTributo(String tributo) {
        this.tributo = tributo;
    }

    public String getTarga() {
        return targa;
    }

    public void setTarga(String targa) {
        this.targa = targa;
    }

    public String getVerbale() {
        return verbale;
    }

    public void setVerbale(String verbale) {
        this.verbale = verbale;
    }

    public String getRefnumber() {
        return refnumber;
    }

    public void setRefnumber(String refnumber) {
        this.refnumber = refnumber;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
