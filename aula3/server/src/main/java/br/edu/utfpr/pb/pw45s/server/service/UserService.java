package br.edu.utfpr.pb.pw45s.server.service;

import br.edu.utfpr.pb.pw45s.server.model.Authority;
import br.edu.utfpr.pb.pw45s.server.model.User;
import br.edu.utfpr.pb.pw45s.server.repository.AuthorityRepository;
import br.edu.utfpr.pb.pw45s.server.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthorityRepository authorityRepository;

    public UserService(UserRepository userRepository,
                       AuthorityRepository authorityRepository) {
        this.userRepository = userRepository;
        passwordEncoder = new BCryptPasswordEncoder();
        this.authorityRepository = authorityRepository;
    }

    public User save(User user) {
        user.setPassword( passwordEncoder.encode(user.getPassword()));
        Set<Authority> authorities = new HashSet<>();
        authorities.add(authorityRepository.findByAuthority("ROLE_USER"));
        user.setAuthorities(authorities);
        return userRepository.save(user);
    }

}
