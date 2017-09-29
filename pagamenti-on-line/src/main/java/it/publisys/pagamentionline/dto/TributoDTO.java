package it.publisys.pagamentionline.dto;

/**
 * @author Francesco A. Tabino
 */
public class TributoDTO extends GenericDTO {

    private String descrizione;
    private String allegati;

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getAllegati() {
        return allegati;
    }

    public void setAllegati(String allegati) {
        this.allegati = allegati;
    }
}
