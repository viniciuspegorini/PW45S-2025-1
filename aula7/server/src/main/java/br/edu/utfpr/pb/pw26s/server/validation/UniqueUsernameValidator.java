package br.edu.utfpr.pb.pw26s.server.validation;

import br.edu.utfpr.pb.pw26s.server.repository.UserRepository;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, String> {

    UserRepository userRepository;

    public UniqueUsernameValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean isValid(String username,
                           ConstraintValidatorContext constraintValidatorContext) {
        if (userRepository.findByUsername(username) == null) {
            return true;
        }
        return false;
    }
}
