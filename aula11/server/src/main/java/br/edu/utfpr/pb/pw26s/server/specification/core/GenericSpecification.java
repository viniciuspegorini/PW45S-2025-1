package br.edu.utfpr.pb.pw26s.server.specification.core;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.time.LocalDate;

public abstract class GenericSpecification<T> implements Specification<T> {

    private SpecSearchCriteria criteria;

    public GenericSpecification(){}

    public GenericSpecification(final SpecSearchCriteria criteria) {
        super();
        this.criteria = criteria;
    }

    public SpecSearchCriteria getCriteria() {
        return criteria;
    }

    @Override
    public Predicate toPredicate(final Root<T> root, final CriteriaQuery<?> query, final CriteriaBuilder builder) {
        switch (criteria.getOperation()) {
            case EQUALITY:
                if (criteria.getValue().toString().equals("true") || criteria.getValue().toString().equals("false")) {
                    return builder.equal(root.get(criteria.getKey()), Boolean.parseBoolean(criteria.getValue().toString()));
                } else {
                    return builder.equal(root.get(criteria.getKey()), criteria.getValue());
                }
            case NEGATION:
                return builder.notEqual(root.get(criteria.getKey()), criteria.getValue());
            case GREATER_THAN:
                return builder.greaterThan(root.get(criteria.getKey()), criteria.getValue().toString());
            case LESS_THAN:
                return builder.lessThan(root.get(criteria.getKey()), criteria.getValue().toString());
            case LIKE:
                return builder.like(root.get(criteria.getKey()), criteria.getValue().toString());
            case STARTS_WITH:
                return builder.like(root.get(criteria.getKey()), criteria.getValue() + "%");
            case ENDS_WITH:
                return builder.like(root.get(criteria.getKey()), "%" + criteria.getValue());
            case CONTAINS:
                return builder.like(root.get(criteria.getKey()), "%" + criteria.getValue() + "%");
            case DATE:
                if (criteria.getValue().toString().length() == 8) {
                    return builder.equal(root.get(criteria.getKey()), LocalDate.of(
                            Integer.parseInt(criteria.getValue().toString().substring(4)),
                            Integer.parseInt(criteria.getValue().toString().substring(2,4)),
                            Integer.parseInt(criteria.getValue().toString().substring(0,2))));
                }else {
                    return builder.between(root.get(criteria.getKey()),
                            LocalDate.of(
                                    Integer.parseInt(criteria.getValue().toString().substring(4,8)),
                                    Integer.parseInt(criteria.getValue().toString().substring(2,4)),
                                    Integer.parseInt(criteria.getValue().toString().substring(0,2))),
                            LocalDate.of(
                                    Integer.parseInt(criteria.getValue().toString().substring(12,16)),
                                    Integer.parseInt(criteria.getValue().toString().substring(10,12)),
                                    Integer.parseInt(criteria.getValue().toString().substring(8,10))));
                }
            default:
                return null;
        }
    }

}

