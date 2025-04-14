package br.edu.utfpr.pb.pw45s.server.security;

public class SecurityConstants {
    public static final String SECRET = "utfpr";
    public static final long EXPIRATION_TIME = 86400000; // 24*60*60*1000
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
}
