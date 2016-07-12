package it.publisys.pagamentionline.domain.impl;

import it.publisys.pagamentionline.domain.AbstractEntity;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * @author vasta
 */
@Entity(name = "tributi")
public class Tributo
        extends AbstractEntity {

    @Column(name = "codice")
    @NotEmpty(message = "Codice obbligatorio")
    private String codice;
    @Column(name = "nome")
    @NotEmpty(message = "Nome obbligatorio")
    private String nome;
    @Column(name = "descrizione", columnDefinition = "TEXT")
    @NotEmpty(message = "Descrizione obbligatoria")
    private String descrizione;
    @Column(name = "responsabile")
    @NotEmpty(message = "Responsabile obbligatorio")
    private String responsabile;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tipologia", referencedColumnName = "id")
    private TipologiaTributo tipologiaTributo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_ente")
    @NotNull
    private Ente ente;

    @Column(name = "anno")
    @NotNull(message = "Anno di Riferimento obbligatorio")
    private Integer anno;

    public Tributo() {
    }

    public Tributo(Long id) {
        super(id);
    }

    public String getResponsabile() {
        return responsabile;
    }

    public void setResponsabile(String responsabile) {
        this.responsabile = responsabile;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public TipologiaTributo getTipologiaTributo() {
        return tipologiaTributo;
    }

    public void setTipologiaTributo(TipologiaTributo tipologiaTributo) {
        this.tipologiaTributo = tipologiaTributo;
    }

    public Integer getAnno() {
        return anno;
    }

    public void setAnno(Integer anno) {
        this.anno = anno;
    }

    public Ente getEnte() {
        return ente;
    }

    public void setEnte(Ente ente) {
        this.ente = ente;
    }

    public String getCodice() {
        return codice;
    }

    public void setCodice(String codice) {
        this.codice = codice;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
