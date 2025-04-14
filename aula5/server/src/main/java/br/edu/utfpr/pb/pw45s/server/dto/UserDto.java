package br.edu.utfpr.pb.pw45s.server.dto;

import br.edu.utfpr.pb.pw45s.server.validation.UniqueUsername;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Data
public class UserDto {

    private long id;

    @UniqueUsername
    @NotNull(message = "{br.edu.utfpr.pb.pw25s.username}")
    @Size(min = 4, max = 255)
    private String username;

    @NotNull
    private String displayName;

    @NotNull
    @Size(min = 6, max = 254)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$")
    private String password;

}
