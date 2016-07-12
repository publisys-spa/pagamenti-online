package it.publisys.pagamentionline.domain.user;

import it.publisys.pagamentionline.domain.AbstractEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

/**
 *
 * @author mcolucci
 */
@Entity(name = "users_roles")
public class UserRole
    extends AbstractEntity {

    @ManyToOne
    private User user;
    @Column(length = 50)
    @NotEmpty(message = "Campo obbligatorio")
    private String authority;

    public UserRole() {
    }

    public UserRole(User user, String authority) {
        this.user = user;
        this.authority = authority;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
