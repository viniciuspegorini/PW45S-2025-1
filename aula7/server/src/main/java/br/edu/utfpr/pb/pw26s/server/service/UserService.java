package br.edu.utfpr.pb.pw26s.server.service;

import br.edu.utfpr.pb.pw26s.server.model.User;
import br.edu.utfpr.pb.pw26s.server.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class UserService {
    private final UserRepository userRepository;
    BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        passwordEncoder = new BCryptPasswordEncoder();
    }

    public User save(User user) {
        user.setPassword( passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

}
