package it.publisys.pagamentionline.domain.impl;

import it.publisys.pagamentionline.domain.AbstractEntity;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import java.util.List;

/**
 * @author Francesco A. Tabino
 */
@Entity(name = "ente")
public class Ente extends AbstractEntity {

    @Column(name = "name")
    @NotEmpty(message = "Nome obbligatorio")
    private String name;
    @Column(name = "fiscal_code")
    @NotEmpty(message = "Codice Fiscale obbligatorio")
    private String fiscalCode;

    @OneToOne(fetch = FetchType.EAGER, mappedBy = "ente")
    private Provider provider;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "ente")
    private List<Tributo> tributi;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFiscalCode() {
        return fiscalCode;
    }

    public void setFiscalCode(String fiscalCode) {
        this.fiscalCode = fiscalCode;
    }

    public List<Tributo> getTributi() {
        return tributi;
    }

    public void setTributi(List<Tributo> tributi) {
        this.tributi = tributi;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }
}
