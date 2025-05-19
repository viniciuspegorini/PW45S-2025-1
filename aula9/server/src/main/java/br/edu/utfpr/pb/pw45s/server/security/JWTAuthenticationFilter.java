package br.edu.utfpr.pb.pw45s.server.security;

import br.edu.utfpr.pb.pw45s.server.model.User;
import br.edu.utfpr.pb.pw45s.server.security.dto.AuthenticationResponse;
import br.edu.utfpr.pb.pw45s.server.security.dto.UserResponseDTO;
import br.edu.utfpr.pb.pw45s.server.service.AuthService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final AuthService authService;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, AuthService authService) {
        this.authenticationManager = authenticationManager;
        this.authService = authService;
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            User credentials = new ObjectMapper().readValue(request.getInputStream(), User.class);
            User user = (User) authService.loadUserByUsername(credentials.getUsername());

            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            credentials.getUsername(),
                            credentials.getPassword(),
                            user.getAuthorities()
                    )
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult)
                                                throws IOException, ServletException {
        User user = (User)
                authService.loadUserByUsername(authResult.getName());
        String token = JWT.create()
                .withSubject(authResult.getName())
                .withExpiresAt(
    new Date(System.currentTimeMillis()+ SecurityConstants.EXPIRATION_TIME
    ))
                .sign(Algorithm.HMAC512(SecurityConstants.SECRET));
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(
                new ObjectMapper().writeValueAsString(
                        new AuthenticationResponse(token, new UserResponseDTO(user)))
        );


    }
}
