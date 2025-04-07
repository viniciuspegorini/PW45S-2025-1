package br.edu.utfpr.pb.pw45s.server.model;

import br.edu.utfpr.pb.pw45s.server.validation.UniqueUsername;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Entity(name = "tb_user")
// @Table(uniqueConstraints = @UniqueConstraint(columnNames = "username"))
@Getter @Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @UniqueUsername
    @NotNull
    @Size(min = 4, max = 255)
    private String username;

    @NotNull
    @NotBlank
    @Size(min = 4, max = 255)
    private String displayName;

    @NotNull
    @Size(min = 6, max = 254)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$")
    private String password;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "tb_user_authorities",
        joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "authority_id", referencedColumnName = "id") )
    private Set<Authority> userAuthorities;

    @Override
    @Transient
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
//        List<Authority> authorities = new ArrayList<>();
//        authorities.addAll(userAuthorities);
//        return authorities;
        //ou
         return new ArrayList<>(userAuthorities);
    }

    @Override
    @Transient
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @Transient
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @Transient
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @Transient
    public boolean isEnabled() {
        return true;
    }

}
