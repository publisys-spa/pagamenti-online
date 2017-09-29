package it.publisys.pagamentionline.domain.impl;

import it.publisys.pagamentionline.domain.AbstractEntity;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * @author Francesco A. Tabino
 *
 * Il file di properties deve essere strutturato in questo modo
 * #configurazione generica GovPay associata al portale unico della Regione Basilicata
    #url = http://serviziclienti.link.it/govpay/PagamentiTelematiciGPAppService
    url=http://serviziclienti.link.it/govpay/PagamentiTelematiciGP/soap/2.3/Portali
    username = pubportale
    password = password

    #urlApp = http://serviziclienti.link.it/govpay/PagamentiTelematiciGPAppService
    urlApp = http://serviziclienti.link.it/govpay/PagamentiTelematiciGP/soap/2.3/Applicazioni
    usernameApp = PSYS
    passwordApp = e987%df

    urlGPRnd= http://serviziclienti.link.it/govpay/PagamentiTelematiciGP/soap/2.3/Rendicontazioni
    usernameGPRnd = PSYS
    passwordGPRnd = e987%df

    codPortale = PSYS
    codApplicazione = PubApp1
    urlBack= http://localhost:8080/pagamentionline/app/pag/pay/storico
    urlReturn= http://localhost:8080/pagamentionline/app/pag/pay/wisp
    pageWisp =http://serviziclienti.link.it/govpay-ndp-sym/wisp.jsp

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
