package guru.sfg.brewery.domain.security;

import guru.sfg.brewery.domain.Customer;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Set;
import java.util.stream.Collectors;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class User implements UserDetails, CredentialsContainer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String username;
    private String password;

    //singular method for added an authority; if the set don`t exist,
    // lombok is provided the code that will do that
    @Singular
    @ManyToMany(cascade = {CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")})
    private Set<Role> roles;

    @ManyToOne(fetch = FetchType.EAGER)
    private Customer customer;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdDate;

    @UpdateTimestamp
    private Timestamp lastModifiedDate;

    @Transient //this property is calculated but not persisted
    public Set<GrantedAuthority> getAuthorities() {
        return this.roles.stream()//get a set of roles, than stream that set
                .map(Role::getAuthorities)//map and get a set of authority
                .flatMap(Set::stream)//get authority from map
                .map(authority -> {
                    return new SimpleGrantedAuthority(authority.getPermission());
                })
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    // if don`t use @Builder.Default the properties wil be null because of builder pattern
    @Builder.Default
    private boolean accountNonExpired = true;
    @Builder.Default
    private boolean accountNonLocked = true;
    @Builder.Default
    private boolean credentialsNonExpired = true;
    @Builder.Default
    private boolean enabled = true;

    //Google 2FA

    @Builder.Default
    private Boolean useGoogle2f = false;

    private String google2FaSecret;

    @Transient
    private Boolean google2faRequired = true;

//

    @Override
    public void eraseCredentials() {
        password = null;
    }
}
