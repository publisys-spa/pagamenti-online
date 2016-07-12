package it.publisys.pagamentionline.domain.impl;

import it.publisys.pagamentionline.domain.AbstractEntity;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

/**
 *
 * @author vasta
 */
@Entity(name = "rata")
public class Rata
    extends AbstractEntity {

    @Column(name = "nome")
    @NotEmpty(message = "Nome obbligatorio")
    private String nome;
    @Column(name = "descrizione")
    @NotEmpty(message = "Descrizione obbligatoria")
    private String descrizione;
    @Column(name = "data_da")
    @NotNull(message = "Data Da obbligatoria")
    @Temporal(TemporalType.DATE)
    protected Date dataDa;
    @Column(name = "data_a")
    @NotNull(message = "Data A obbligatoria")
    @Temporal(TemporalType.DATE)
    protected Date dataA;
    @Column(name = "causale")
    @NotEmpty(message = "Causale obbligatoria")
    private String causale;
    @Column(name = "custom_id")
    private String customId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tributo", referencedColumnName = "id")
    private Tributo tributo;

    public Rata() {
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

    public Date getDataDa() {
        return dataDa;
    }

    public void setDataDa(Date dataDa) {
        this.dataDa = dataDa;
    }

    public Date getDataA() {
        return dataA;
    }

    public void setDataA(Date dataA) {
        this.dataA = dataA;
    }

    public String getCausale() {
        return causale;
    }

    public void setCausale(String causale) {
        this.causale = causale;
    }

    public String getCustomId() {
        return customId;
    }

    public void setCustomId(String customId) {
        this.customId = customId;
    }

    public Tributo getTributo() {
        return tributo;
    }

    public void setTributo(Tributo tributo) {
        this.tributo = tributo;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
