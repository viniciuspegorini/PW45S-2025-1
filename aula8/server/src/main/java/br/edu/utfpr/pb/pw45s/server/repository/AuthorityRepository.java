package br.edu.utfpr.pb.pw45s.server.repository;

import br.edu.utfpr.pb.pw45s.server.model.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {
    Authority findByAuthority(String authority);

}
