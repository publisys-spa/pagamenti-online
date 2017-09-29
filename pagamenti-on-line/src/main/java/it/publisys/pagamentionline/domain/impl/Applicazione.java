package it.publisys.pagamentionline.domain.impl;

import it.publisys.pagamentionline.domain.AbstractEntity;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 *
 * @author acoviello
 */
@Entity(name = "applicazione")
public class Applicazione
    extends AbstractEntity {

    @Column(name = "codice")
    @NotEmpty(message = "Codice obbligatorio")
    private String codice;
    @Column(name = "descrizione")
    @NotEmpty(message = "Descrizione obbligatoria")
    private String descrizione;
    @Column(name = "responsabile")
    private String responsabile;


    public Applicazione() {
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public String getCodice() {
        return codice;
    }

    public void setCodice(String codice) {
        this.codice = codice;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getResponsabile() {
        return responsabile;
    }

    public void setResponsabile(String responsabile) {
        this.responsabile = responsabile;
    }
}
