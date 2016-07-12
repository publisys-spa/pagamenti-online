package it.publisys.pagamentionline.domain.impl;

import it.publisys.pagamentionline.domain.AbstractEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

/**
 *
 * @author vasta
 */
@Entity(name = "tipologie_tributi")
public class TipologiaTributo
    extends AbstractEntity {

    @Column(name = "nome")
    @NotEmpty(message = "Nome obbligatorio")
    private String nome;
    @Column(name = "descrizione")
    @NotEmpty(message = "Descrizione obbligatoria")
    private String descrizione;
    @Column(name = "codiceRadice")
    @NotEmpty(message = "Codice radice obbligatorio")
    private String codiceRadice;
    @Column(name = "tipo")
    @NotEmpty(message = "Tipo obbligatorio")
    private String tipo;

    public TipologiaTributo() {
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCodiceRadice() {
        return codiceRadice;
    }

    public void setCodiceRadice(String codiceRadice) {
        this.codiceRadice = codiceRadice;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
