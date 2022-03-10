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

        // ~보다 크다 <
        if(criteria.getCondition().equalsIgnoreCase("up")){
            return builder.greaterThanOrEqualTo(
                    root.<String> get(criteria.getKey()), criteria.getValue().toString());
            // ~보다 작다 >
        }else if(criteria.getCondition().equalsIgnoreCase("down")){
            return builder.lessThanOrEqualTo(
                    root.<String> get(criteria.getKey()), criteria.getValue().toString());
            // ~와 같을경우
        }else if(criteria.getCondition().equalsIgnoreCase("equals")){
            // 해당 타입이 String 이면서
            if (root.get(criteria.getKey()).getJavaType() == String.class) {
                // ~가 포함되어있다.
                return builder.like(
                        root.<String>get(criteria.getKey()), "%" + criteria.getValue() + "%");
            } else {
                // ~와 같다.
                return builder.equal(root.get(criteria.getKey()), criteria.getValue());
            }
        }
        return null;
    }
}
