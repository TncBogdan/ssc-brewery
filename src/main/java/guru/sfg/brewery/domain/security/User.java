package guru.sfg.brewery.domain.security;

import lombok.*;

import javax.persistence.*;
import java.util.Set;
import java.util.stream.Collectors;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String username;
    private String password;

    //singular method for added an authority; if the set don`t exist,
    // lombok is provided the code that will do that
    @Singular
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")})
    private Set<Role> roles;

    @Transient //this property is calculated but not persisted
    private Set<Authority> authorities;

    public Set<Authority> getAuthorities() {
        return this.roles.stream()//get a set of roles, than stream that set
                .map(Role::getAuthorities)//map and get a set of authority
                .flatMap(Set::stream)//get authority from map
                .collect(Collectors.toSet());
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
}
