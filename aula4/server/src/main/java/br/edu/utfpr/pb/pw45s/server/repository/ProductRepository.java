package br.edu.utfpr.pb.pw45s.server.repository;

import br.edu.utfpr.pb.pw45s.server.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
