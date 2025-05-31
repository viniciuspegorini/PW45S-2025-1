package br.edu.utfpr.pb.pw26s.server.model;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import jakarta.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Authority implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String authority;

}