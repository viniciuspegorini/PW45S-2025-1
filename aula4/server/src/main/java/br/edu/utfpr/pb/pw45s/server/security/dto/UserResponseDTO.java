package br.edu.utfpr.pb.pw45s.server.security.dto;

import br.edu.utfpr.pb.pw45s.server.model.Authority;
import br.edu.utfpr.pb.pw45s.server.model.User;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.HashSet;
import java.util.Set;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponseDTO {
    private String username;
    private String displayName;
    private Set<AuthorityResponseDTO> authorities;

    public UserResponseDTO(User user) {
        this.username = user.getUsername();
        this.displayName = user.getDisplayName();
        this.authorities = new HashSet<>();
        for (GrantedAuthority authority : user.getAuthorities()) {
            authorities.add(
                    new AuthorityResponseDTO(
                            authority.getAuthority()));
        }
    }
}
