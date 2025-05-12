package br.edu.utfpr.pb.pw45s.server.security.social;

import br.edu.utfpr.pb.pw45s.server.model.AuthProvider;
import br.edu.utfpr.pb.pw45s.server.model.User;
import br.edu.utfpr.pb.pw45s.server.repository.AuthorityRepository;
import br.edu.utfpr.pb.pw45s.server.repository.UserRepository;
import br.edu.utfpr.pb.pw45s.server.security.SecurityConstants;
import br.edu.utfpr.pb.pw45s.server.security.dto.AuthenticationResponse;
import br.edu.utfpr.pb.pw45s.server.security.dto.UserResponseDTO;
import br.edu.utfpr.pb.pw45s.server.service.UserService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.api.client.json.webtoken.JsonWebToken;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashSet;

@RestController
@RequestMapping("auth-social")
@Slf4j
public class AuthController {
    private final GoogleTokenVerifier verifier;
    private final UserService userService;
    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;

    public AuthController(GoogleTokenVerifier verifier,
                          UserService userService,
                          UserRepository userRepository,
                          AuthorityRepository authorityRepository) {
        this.verifier = verifier;
        this.userService = userService;
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
    }

    @PostMapping
    public ResponseEntity<AuthenticationResponse> auth(
            HttpServletRequest request,
            HttpServletResponse response) {
        String idToken = request.getHeader("Auth-Id-Token");
        if (idToken != null) {
            final JsonWebToken.Payload payload;
            try {
                payload = verifier.verify(idToken.replace(
                        SecurityConstants.TOKEN_PREFIX, ""));
                if (payload != null) {
                    String username = (String) payload.get("email");
                    User user = userRepository.findByUsername(username);
                    if (user == null) {
                        user = new User();
                        user.setUsername(username);
                        user.setPassword("P4ssword");
                        user.setDisplayName((String) payload.get("name"));
                        user.setProvider(AuthProvider.google);

                        user.setUserAuthorities(new HashSet<>());
                        user.getUserAuthorities().add(
                                authorityRepository.findByAuthority("ROLE_USER")
                        );
                        userService.save(user);
                    }
                    String token = JWT.create()
                            .withSubject(user.getUsername())
                            .withExpiresAt(
                        new Date(System.currentTimeMillis() +
                        SecurityConstants.EXPIRATION_TIME
                            ))
                        .sign(Algorithm.HMAC512(SecurityConstants.SECRET.getBytes()));

                    return ResponseEntity.ok(
                            new AuthenticationResponse(token,
                                    new UserResponseDTO(user)));
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }
}
