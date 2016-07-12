package it.publisys.pagamentionline.domain.impl;

import it.publisys.pagamentionline.domain.AbstractEntity;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * @author Francesco A. Tabino
 */
@Entity(name = "provider")
public class Provider extends AbstractEntity {

    @Column(name = "name")
    @NotEmpty(message = "Nome obbligatorio")
    private String name;
    @Column(name = "properties", columnDefinition = "TEXT")
    @NotEmpty(message = "Properties obbligatoria")
    private String properties;

    @OneToOne(fetch = FetchType.EAGER, cascade=CascadeType.MERGE)
    @JoinColumn(name = "fk_ente", referencedColumnName = "id")
    @NotNull
    private Ente ente;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    public Ente getEnte() {
        return ente;
    }

    public void setEnte(Ente ente) {
        this.ente = ente;
    }
}
