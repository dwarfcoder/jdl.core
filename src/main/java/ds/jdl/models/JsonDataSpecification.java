package ds.jdl.models;

import ds.jdl.entities.JsonEntityBase;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class JsonDataSpecification<TEntity extends JsonEntityBase> implements Specification<TEntity> {
    private Request request;

    @Override
    public Predicate toPredicate(Root<TEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        if(request == null || request.getWhere() == null) {
            return null;
        }

        List<Predicate> ands = new ArrayList<>();
        if(request.getWhere().and != null && !request.getWhere().and.isEmpty()) {
            for (Condition c : request.getWhere().and) {
                Predicate and = getPredicate(root, c, criteriaBuilder);
                ands.add(and);
            }
        }

        List<Predicate> ors = new ArrayList<>();
        if(request.getWhere().or != null && !request.getWhere().or.isEmpty()) {
            for (Condition c : request.getWhere().or) {
                Predicate or = getPredicate(root, c, criteriaBuilder);
                ors.add(or);
            }
        }

        Predicate combinedAnd = criteriaBuilder.and(ands.toArray(new Predicate[0]));
        if(ors.isEmpty()) {
            return combinedAnd;
        } else {
            Predicate combinedOr = criteriaBuilder.and(ors.toArray(new Predicate[0]));
            Predicate combined = criteriaBuilder.or(combinedAnd, combinedOr);
            return combined;
        }
    }

    private Predicate getPredicate(Root<TEntity> root, Condition condition, CriteriaBuilder criteriaBuilder) {
        String[] fields = condition.getFieldName().split("[.]", 0);
        if(fields == null || fields.length == 0) {
            return null;
        }

        List<Expression> expressions = new ArrayList<>();
        expressions.add(root.<Object>get("data"));
        expressions.addAll(Arrays.stream(fields).map(x -> criteriaBuilder.literal(x)).collect(Collectors.toList()));

        Expression[] expressionArr = expressions.toArray(new Expression[0]);

        switch (condition.getOperation()) {
            case LessThan:
                return criteriaBuilder.lessThan (
                        criteriaBuilder
                                .function("jsonb_extract_path_text", String.class, expressionArr)
                        , condition.getValue()
                );
            case GreaterThan:
                return criteriaBuilder.greaterThan(
                        criteriaBuilder
                                .function("jsonb_extract_path_text", String.class, expressionArr)
                        , condition.getValue()
                );
            case Equals:
                return criteriaBuilder.equal(
                        criteriaBuilder
                                .function("jsonb_extract_path_text", Object.class, expressionArr)
                        , condition.getValue()
                );
            case LessOrEqual:
                return criteriaBuilder.lessThanOrEqualTo(
                        criteriaBuilder
                                .function("jsonb_extract_path_text", String.class, expressionArr)
                        , condition.getValue()
                );
            case GreaterOrEqual:
                return criteriaBuilder.greaterThanOrEqualTo(
                        criteriaBuilder
                                .function("jsonb_extract_path_text", String.class, expressionArr)
                        , condition.getValue()
                );
            case Like:
                return criteriaBuilder.like(
                        criteriaBuilder
                                .function("jsonb_extract_path_text", String.class, expressionArr)
                        , condition.getValue()
                );
        }

        return null;
    }
}
