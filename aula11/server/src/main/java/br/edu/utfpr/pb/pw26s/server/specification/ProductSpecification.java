package br.edu.utfpr.pb.pw26s.server.specification;

import br.edu.utfpr.pb.pw26s.server.model.Product;
import br.edu.utfpr.pb.pw26s.server.specification.core.GenericSpecification;
import br.edu.utfpr.pb.pw26s.server.specification.core.SpecSearchCriteria;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecification extends GenericSpecification<Product> implements Specification<Product> {

    public ProductSpecification(final SpecSearchCriteria criteria) {
        super(criteria);
    }

}
