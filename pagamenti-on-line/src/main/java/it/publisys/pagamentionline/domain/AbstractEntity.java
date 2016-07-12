package it.publisys.pagamentionline.domain;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 *
 * @author mcolucci
 */
@MappedSuperclass
public abstract class AbstractEntity
    implements IEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;
    @Version
    protected Long version;
    @Column(name = "logc_user", length = 100)
    protected String logcUser;
    @Column(name = "logc_date")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date logcDate;
    @Column(name = "logu_user", length = 100)
    protected String loguUser;
    @Column(name = "logu_date")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date loguDate;
    @Column(name = "logd_user", length = 100)
    protected String logdUser;
    @Column(name = "logd_date")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date logdDate;

    public AbstractEntity() {
    }

    public AbstractEntity(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getVersion() {
        return version;
    }

    @Override
    public void setVersion(Long version) {
        this.version = version;
    }

    public String getLogcUser() {
        return logcUser;
    }

    public void setLogcUser(String logcUser) {
        this.logcUser = logcUser;
    }

    public Date getLogcDate() {
        return logcDate;
    }

    public void setLogcDate(Date logcDate) {
        this.logcDate = logcDate;
    }

    public String getLoguUser() {
        return loguUser;
    }

    public void setLoguUser(String loguUser) {
        this.loguUser = loguUser;
    }

    public Date getLoguDate() {
        return loguDate;
    }

    public void setLoguDate(Date loguDate) {
        this.loguDate = loguDate;
    }

    public String getLogdUser() {
        return logdUser;
    }

    public void setLogdUser(String logdUser) {
        this.logdUser = logdUser;
    }

    public Date getLogdDate() {
        return logdDate;
    }

    public void setLogdDate(Date logdDate) {
        this.logdDate = logdDate;
    }

    @Override
    public void setLogCreate(String user) {
        this.setLogcUser(user);
        this.setLogcDate(new Date());
    }

    @Override
    public void setLogUpdate(String user) {
        this.setLoguUser(user);
        this.setLoguDate(new Date());
    }

    @Override
    public void setLogDelete(String user) {
        this.setLogdUser(user);
        this.setLogdDate(new Date());
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
