package br.edu.utfpr.pb.pw26s.server.repository;

import br.edu.utfpr.pb.pw26s.server.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
