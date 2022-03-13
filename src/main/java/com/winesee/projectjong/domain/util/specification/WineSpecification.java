package com.winesee.projectjong.domain.util.specification;

import com.winesee.projectjong.domain.wine.Wine;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@RequiredArgsConstructor
public class WineSpecification implements Specification<Wine> {

    private final SearchCriteria criteria;

    @Override
    public Predicate toPredicate(Root<Wine> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

        switch (criteria.getCondition()) {
            case "equals" : // 문자열 길이와 크기가 같다.
                return builder.equal(root.get(criteria.getKey()), criteria.getValue());
            case "greaterthan" : // 크다
                return builder.greaterThan(root.<String> get(criteria.getKey()), criteria.getValue().toString());
            case "lessthan" : // 작다
                return builder.lessThan(root.<String> get(criteria.getKey()), criteria.getValue().toString());
            case "like" : // 내용이 완전히 같다.
                return builder.like(root.<String> get(criteria.getKey()), criteria.getKey());
            case "contains" : // 포함
                return builder.like(root.<String> get(criteria.getKey()), "%" + criteria.getValue().toString() + "%");
            default:
                return null;
        }
    }
}
