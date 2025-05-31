package br.edu.utfpr.pb.pw26s.server.specification.core;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.function.Function;

import org.springframework.data.jpa.domain.Specification;


public class GenericSpecificationsBuilder<T> {


    private final List<SpecSearchCriteria> params;

    public GenericSpecificationsBuilder() {
        params = new ArrayList<>();
    }

    public final GenericSpecificationsBuilder<T> with(final String key, final String operation, final Object value, final String prefix, final String suffix) {
        return with(null, key, operation, value, prefix, suffix);
    }

    public final GenericSpecificationsBuilder<T> with(final String orPredicate, final String key, final String operation, final Object value, final String prefix, final String suffix) {
        SearchOperation op = SearchOperation.getSimpleOperation(operation.charAt(0));
        if (op != null) {
            if (op == SearchOperation.EQUALITY) {
                final boolean startWithAsterisk = prefix != null && prefix.contains(SearchOperation.ZERO_OR_MORE_REGEX);
                final boolean endWithAsterisk = suffix != null && suffix.contains(SearchOperation.ZERO_OR_MORE_REGEX);

                if (startWithAsterisk && endWithAsterisk) {
                    op = SearchOperation.CONTAINS;
                } else if (startWithAsterisk) {
                    op = SearchOperation.ENDS_WITH;
                } else if (endWithAsterisk) {
                    op = SearchOperation.STARTS_WITH;
                }
            }
            params.add(new SpecSearchCriteria(orPredicate, key, op, value));
        }
        return this;
    }

    public <T> Specification<T> build(Function<SpecSearchCriteria, Specification<T>> mappingToSpecification) {
        if (params.size() == 0) {
            return null;
        }

        List<Specification<T>> specs = new ArrayList<>();
        for (SpecSearchCriteria param : params) {
            specs.add(mappingToSpecification.apply(param));
        }

        Specification<T> result = specs.get(0);
        for (int i = 1; i < specs.size(); i++) {
            result = params.get(i).isOrPredicate()
                    ? Specification.where(result).or(specs.get(i))
                    : Specification.where(result).and(specs.get(i));
        }
        return result;
    }

    public final GenericSpecificationsBuilder<T> with(SpecSearchCriteria criteria) {
        params.add(criteria);
        return this;
    }

    public Specification<T> build(Deque<?> parse, Object aNew) {
        return null;
    }

}