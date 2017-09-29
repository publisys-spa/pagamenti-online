package it.publisys.pagamentionline.domain.impl;

import it.publisys.pagamentionline.domain.AbstractEntity;
import it.publisys.pagamentionline.domain.user.User;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.NumberFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author vasta
 */
@Entity(name = "pagamenti")
@Table(name = "pagamenti",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "pagamenti_pid_uni",
                        columnNames = {"pid"})

        })
public class Pagamento
        extends AbstractEntity {

    @Column(name = "pid")
    private String pid;
    @Column(name = "data_pagamento")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataPagamento;
    @Column(name = "tipologia")
    private String tipologia;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "esecutore", referencedColumnName = "id")
    private User esecutore;
    @Column(name = "atto_accertamento")
    private String attoAccertamento;
    @Column(name = "causale", columnDefinition = "TEXT")
    @NotNull(message = "Causale obbligatoria")
    @NotBlank(message = "Causale obbligatoria")
    private String causale;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tributo", referencedColumnName = "id")
    private Tributo tributo;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ente", referencedColumnName = "id")
    private Ente ente;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rata", referencedColumnName = "id")
    private Rata rata;
    @NumberFormat(style = NumberFormat.Style.CURRENCY, pattern = "#,###.00")
    @Column(name = "importo")
    @NotNull(message = "Importo obbligatorio")
    private Double importo;
    @NumberFormat(style = NumberFormat.Style.CURRENCY, pattern = "#,###.00")
    @Column(name = "importo_commissione")
    private Double importoCommissione;

    @Column(name = "stato_pagamento")
    private String statoPagamento;
    @Column(name = "iur")
    private String iur;
    @Column(name = "codPsp")
    private String codPsp;

    @Column(name = "key_wisp")
    private String keyWisp;

    @Column(name = "iuv")
    private String iuv;
    @Column(name = "ccp")
    private String ccp;
    @Column(name = "date_processed")
    private String dateProcessed;
    @Column(name = "status_response")
    private String statusResponse;
    @Column(name = "codVersamentoEnte")
    private String codVersamentoEnte;
    @Column(name = "id_sessione")
    private String idSessione;


    public Pagamento() {
    }

    public Pagamento(Long id) {
        super(id);
    }

    public Double getImportoCommissione() {
        return importoCommissione;
    }

    public void setImportoCommissione(Double importoCommissione) {
        this.importoCommissione = importoCommissione;
    }

    public String getIuv() {
        return iuv;
    }

    public void setIuv(String iuv) {
        this.iuv = iuv;
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

    public String getTipologia() {
        return tipologia;
    }

    public void setTipologia(String tipologia) {
        this.tipologia = tipologia;
    }

    public User getEsecutore() {
        return esecutore;
    }

    public void setEsecutore(User esecutore) {
        this.esecutore = esecutore;
    }

    public String getAttoAccertamento() {
        return attoAccertamento;
    }

    public void setAttoAccertamento(String attoAccertamento) {
        this.attoAccertamento = attoAccertamento;
    }

    public String getCausale() {
        return causale;
    }

    public void setCausale(String causale) {
        this.causale = causale;
    }

    public Tributo getTributo() {
        return tributo;
    }

    public void setTributo(Tributo tributo) {
        this.tributo = tributo;
    }

    public Rata getRata() {
        return rata;
    }

    public void setRata(Rata rata) {
        this.rata = rata;
    }

    public Double getImporto() {
        return importo;
    }

    public void setImporto(Double importo) {
        this.importo = importo;
    }

    public Ente getEnte() {
        return ente;
    }

    public void setEnte(Ente ente) {
        this.ente = ente;
    }

    public String getStatoPagamento() {
        return statoPagamento;
    }

    public void setStatoPagamento(String statoPagamento) {
        this.statoPagamento = statoPagamento;
    }

    public String getIur() {
        return iur;
    }

    public void setIur(String iur) {
        this.iur = iur;
    }

    public String getKeyWisp() {
        return keyWisp;
    }

    public void setKeyWisp(String keyWisp) {
        this.keyWisp = keyWisp;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public String getCcp() {
        return ccp;
    }

    public void setCcp(String ccp) {
        this.ccp = ccp;
    }

    public String getCodVersamentoEnte() {
        return codVersamentoEnte;
    }

    public void setCodVersamentoEnte(String codVersamentoEnte) {
        this.codVersamentoEnte = codVersamentoEnte;
    }

    public String getCodPsp() {
        return codPsp;
    }

    public void setCodPsp(String codPsp) {
        this.codPsp = codPsp;
    }

    public String getIdSessione() {
        return idSessione;
    }

    public void setIdSessione(String idSessione) {
        this.idSessione = idSessione;
    }
}
