package com.winesee.projectjong.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * 유저 Repository
 * DB에 접근하는 소스코드를 모아둔 interface
 * JPA를 사용하며, JpaRepository 인터페이스를 상속받아 제네릭을 통해 괸리하고자 하는 클래스로 바뀐다.
 * ID 필드 타입을 <Entity테이블, PK>(엔티티 ID 유형)와 같이 넣어주게되면 자동으로 DB와 CRUD 연결을 할 수 있는 메소드를 생성해준다.
 * CUD와 같은 경우는 다른 테이블간의 조인 을 잘 수행하지 않기 때문에 그대로 사용하지만 Read와 같은 경우엔 여러 테이블과 조인이 필요함으로
 * @Query 어노테이션으로 직접 쿼리를 작성하여 사용.
 */
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    User findByEmail(String email);

    User findByName(String name);

    List<User> findAllByUsernameOrEmailOrName(String username, String email, String name);

}
