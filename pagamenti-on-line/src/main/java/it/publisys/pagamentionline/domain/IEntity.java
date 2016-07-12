package it.publisys.pagamentionline.domain;

import java.io.Serializable;

/**
 *
 * @author mcolucci
 */
public interface IEntity extends Serializable {

    Long getId();

    void setId(Long id);

    Long getVersion();

    void setVersion(Long version);

    void setLogCreate(String user);

    void setLogUpdate(String user);

    void setLogDelete(String user);
}
