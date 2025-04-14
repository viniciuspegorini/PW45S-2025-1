package br.edu.utfpr.pb.pw45s.server.security.oauth2.user;

import br.edu.utfpr.pb.pw45s.server.error.OAuth2AuthenticationProcessingException;
import br.edu.utfpr.pb.pw45s.server.model.AuthProvider;

import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        if(registrationId.equalsIgnoreCase(AuthProvider.google.toString())) {
            return new GoogleOAuth2UserInfo(attributes);
        } else {
            throw new OAuth2AuthenticationProcessingException(
                    "Desculpe! A autenticação com " + registrationId + " não é suportada.");
        }
    }
}
