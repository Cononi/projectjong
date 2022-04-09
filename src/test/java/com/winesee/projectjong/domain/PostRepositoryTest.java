package com.winesee.projectjong.domain;

import com.winesee.projectjong.domain.board.Post;
import com.winesee.projectjong.domain.board.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Test
    public void PostBestFiveList(){
        // Given : 테스트에서 구체화하고자 하는 행동을 시작하기 전에 테스트 상태를 설명하는 부분

        // When : 구체화하고자 하는 그 행동
        Pageable pageable = PageRequest.of(0,5);
        List<Post> post = postRepository.findAllWithCommentGraph(pageable);

        // Then : 어떤 특정한 행동 때문에 발생할거라고 예상되는 변화에 대한 설명

        // 케이스 내용 확인용.
        post.forEach(c -> System.out.println(c.getPostId()));
//        // 결과
//        assertTrue(post.iterator().next().getPostId().toLowerCase().contains("krug".toLowerCase()));
    }
}
