package com.winesee.projectjong.domain.util.specification;

import com.winesee.projectjong.domain.wine.Wine;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Slf4j
public class UserSpecificationsBuilder {
    private final List<SearchCriteria> params = new ArrayList<>();

    public UserSpecificationsBuilder with(
            String key, String condition, Object value) {
            params.add(new SearchCriteria(key, condition, value));
        return this;
    }

    public Specification<Wine> build() {
        if (params.size() == 0) {
            return null;
        }

        Specification<Wine> result = new WineSpecification(params.get(0));

        for (int i = 1; i < params.size(); i++) {
            result = Specification.where(result).and(new WineSpecification(params.get(i)));
        }

        return result;
    }
}
