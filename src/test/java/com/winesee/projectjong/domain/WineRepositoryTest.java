package com.winesee.projectjong.domain;


import com.winesee.projectjong.ProjectjongApplication;
import com.winesee.projectjong.domain.util.specification.SearchCriteria;
import com.winesee.projectjong.domain.util.specification.WineSpecification;
import com.winesee.projectjong.domain.wine.Wine;
import com.winesee.projectjong.domain.wine.WineRepository;
import com.winesee.projectjong.service.email.MailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@PropertySource("classpath:application.yml")
public class WineRepositoryTest {

    @Autowired
    private WineRepository wineRepository;

    @Test
    public void WineCategoryListSearchTest(){

        // Given : 테스트에서 구체화하고자 하는 행동을 시작하기 전에 테스트 상태를 설명하는 부분
        // 필터
        WineSpecification spec =
                new WineSpecification(new SearchCriteria("displayName", "equals", "krug"));
        // 페이징
        Pageable pageable = PageRequest.of(0, 5, Sort.Direction.DESC, "displayName");

        // When : 구체화하고자 하는 그 행동
        Page<Wine> wine = wineRepository.findAll(spec, pageable);

        // Then : 어떤 특정한 행동 때문에 발생할거라고 예상되는 변화에 대한 설명

        // 케이스 내용 확인용.
        wine.forEach(c -> System.out.println(c.getDisplayName()));
        // 결과
        assertTrue(wine.iterator().next().getDisplayName().toUpperCase().contains("krug".toUpperCase()));
    }

}
