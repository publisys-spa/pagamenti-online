package it.publisys.pagamentionline.domain.user;

import it.publisys.pagamentionline.domain.AbstractEntity;
import java.util.Collection;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 *
 * @author mcolucci
 */
@Entity(name = "users")
public class User
    extends AbstractEntity
    implements UserDetails {

    @Column(unique = true, length = 50)
    @NotEmpty(message = "Campo obbligatorio")
    private String username;
    @NotEmpty(message = "Campo obbligatorio")
    private String password;
    @NotEmpty(message = "Campo obbligatorio")
    private String email;
    @NotEmpty(message = "Campo obbligatorio")
    private String fiscalcode;
    private String firstname;
    private String lastname;
    private boolean enabled;
    //
    private String keyRequest;
    //
    @Transient
    private List<GrantedAuthority> authorities;

    public User() {
    }

    public User(Long id) {
        super(id);
    }

    public String getKeyRequest() {
        return keyRequest;
    }

    public void setKeyRequest(String keyRequest) {
        this.keyRequest = keyRequest;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFiscalcode() {
        return fiscalcode;
    }

    public void setFiscalcode(String fiscalcode) {
        this.fiscalcode = fiscalcode;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
