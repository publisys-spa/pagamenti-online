package it.publisys.pagamentionline.domain.user;

import it.publisys.pagamentionline.domain.AbstractEntity;
import it.publisys.pagamentionline.domain.enumeration.MailStatusEnum;
import it.publisys.pagamentionline.domain.enumeration.MailTypeEnum;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

/**
 *
 * @author mcolucci
 */
@Entity(name = "mails")
public class Mail
    extends AbstractEntity {

    @Column(nullable = false)
    @NotEmpty(message = "nominativo campo obbligatorio")
    private String name;
    @Column(nullable = false)
    private String fiscalcode;
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    @NotEmpty(message = "{0} campo obbligatorio")
    @Email(message = "{0} non valida")
    private String email;
    @Column(nullable = false, columnDefinition = "TEXT")
    @NotEmpty(message = "testo campo obbligatorio")
    private String text;
    @Temporal(TemporalType.TIMESTAMP)
    private Date date = new Date();
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MailStatusEnum status = MailStatusEnum.READY;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MailTypeEnum type;

    public Mail() {
    }

    public Mail(String username) {
        this.username = username;
    }

    public Mail(String name, String fiscalcode, String username, String email) {
        this.name = name;
        this.fiscalcode = fiscalcode;
        this.username = username;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFiscalcode() {
        return fiscalcode;
    }

    public void setFiscalcode(String fiscalcode) {
        this.fiscalcode = fiscalcode;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public MailStatusEnum getStatus() {
        return status;
    }

    public void setStatus(MailStatusEnum status) {
        this.status = status;
    }

    public MailTypeEnum getType() {
        return type;
    }

    public void setType(MailTypeEnum type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
