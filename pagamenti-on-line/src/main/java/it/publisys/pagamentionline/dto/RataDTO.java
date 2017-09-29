package it.publisys.pagamentionline.dto;

/**
 * @author Francesco A. Tabino
 */
public class RataDTO extends GenericDTO {

    private String causale;
    private String descrizione;
    private String note;

    public String getCausale() {
        return causale;
    }

    public void setCausale(String causale) {
        this.causale = causale;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
