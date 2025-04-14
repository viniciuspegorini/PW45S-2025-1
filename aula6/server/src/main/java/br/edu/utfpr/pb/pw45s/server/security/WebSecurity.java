package br.edu.utfpr.pb.pw45s.server.security;

import br.edu.utfpr.pb.pw45s.server.security.oauth2.CustomOAuth2UserService;
import br.edu.utfpr.pb.pw45s.server.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import br.edu.utfpr.pb.pw45s.server.security.oauth2.OAuth2AuthenticationFailureHandler;
import br.edu.utfpr.pb.pw45s.server.security.oauth2.OAuth2AuthenticationSuccessHandler;
import br.edu.utfpr.pb.pw45s.server.service.AuthService;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@EnableWebSecurity
@Configuration
public class WebSecurity {

    private final AuthService authService;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    public WebSecurity(AuthService authService,
                       AuthenticationEntryPoint authenticationEntryPoint,
                       CustomOAuth2UserService customOAuth2UserService,
                       OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler,
                       OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler) {
        this.authService = authService;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.customOAuth2UserService = customOAuth2UserService;
        this.oAuth2AuthenticationSuccessHandler = oAuth2AuthenticationSuccessHandler;
        this.oAuth2AuthenticationFailureHandler = oAuth2AuthenticationFailureHandler;
    }


    @Bean
    @SneakyThrows
    public SecurityFilterChain filterChain(HttpSecurity http) {
        // authenticationManager -> responsável pela autenticação dos usuários
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(authService)
                .passwordEncoder( passwordEncoder() );
        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

        //Configuração para funcionar o console do H2.
        http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));
        http.csrf(AbstractHttpConfigurer::disable);

        // Adiciona configuração de CORS
        http.cors(cors -> corsConfigurationSource());


        http.exceptionHandling(exceptionHandling -> exceptionHandling.authenticationEntryPoint(authenticationEntryPoint));
        http.authorizeHttpRequests((authorize) -> authorize
                .requestMatchers(antMatcher(HttpMethod.POST, "/users/**")).permitAll()
                .requestMatchers(antMatcher("/h2-console/**")).permitAll()
                .requestMatchers(antMatcher("/error/**")).permitAll()
                .requestMatchers(antMatcher("/actuator/**")).permitAll()
                .requestMatchers(antMatcher("/oauth2/**")).permitAll()
                .requestMatchers(antMatcher("/auth/**")).permitAll()

                // Somente usuários com permissão de admin podem acessar /products (qualquer requisição HTTP)
                .requestMatchers(antMatcher("/products/**")).hasAnyRole("ADMIN")
                .requestMatchers(antMatcher(HttpMethod.GET, "/categories/**")).hasAnyRole("ADMIN", "USER")
                .requestMatchers(antMatcher("/categories/**")).hasAnyRole("USER")
                .requestMatchers(antMatcher("/users/**")).hasAnyRole("ADMIN")
                .anyRequest().authenticated()
        );

        http.oauth2Login((oauth2Login) -> oauth2Login
                .authorizationEndpoint((authorizationEndpoint) -> authorizationEndpoint
                        .baseUri("/oauth2/authorize")
                        .authorizationRequestRepository(cookieAuthorizationRequestRepository()))
                .redirectionEndpoint( redirectionEndpoint ->
                        redirectionEndpoint.baseUri("/oauth2/callback/*"))
                .userInfoEndpoint(userInfoEndpoint ->
                        userInfoEndpoint.userService(customOAuth2UserService))
                .successHandler(oAuth2AuthenticationSuccessHandler)
                .failureHandler(oAuth2AuthenticationFailureHandler)
        );

        http.authenticationManager(authenticationManager)
                //Filtro de Autenticação
                .addFilter(new JWTAuthenticationFilter(authenticationManager, authService))
                //Filtro de Autorização
                .addFilter(new JWTAuthorizationFilter(authenticationManager, authService))
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository() {
        return new HttpCookieOAuth2AuthorizationRequestRepository();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "TRACE", "CONNECT"));
        configuration.setAllowedHeaders(List.of("Authorization","x-xsrf-token",
                                    "Access-Control-Allow-Headers", "Origin",
                                    "Accept", "X-Requested-With", "Content-Type",
                                    "Access-Control-Request-Method",
                                    "Access-Control-Request-Headers", "Auth-Id-Token"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
