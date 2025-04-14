package br.edu.utfpr.pb.pw45s.server.repository;

import br.edu.utfpr.pb.pw45s.server.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

}
