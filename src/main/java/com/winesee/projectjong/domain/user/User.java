package com.winesee.projectjong.domain.user;

import com.winesee.projectjong.domain.basedefault.BaseTime;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Jhong
 * @version 1.0
 * @since 2022-02-14
 * 유저 Entity
 * Entity의 값이 변하면 Repository 클래스의 Entity Manager의 flush가 호출될 때 DB에 값이 반영되고, 이는 다른 로직들에도 영향 미친다.
 * View와 통신하면서 필연적으로 데이터의 변경이 많은 DTO클래스를 분리해주어야 한다.
 */
@Entity
@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@Table(
        name="user",
        uniqueConstraints={
                @UniqueConstraint(columnNames={"name", "username", "email"})
        }
)
public class User extends BaseTime implements Serializable, Principal, UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String username;
    private String password;
    private String email;
    private Boolean isActive;
    private Boolean isNonLocked;
    private Boolean isEmailEnabled;
    private LocalDateTime lastLoginDate; // 마지막 로그인 시간
//    /*
//    회원 정보를 가져올때 즉시 권한도 가져온다. (원자성)
//     fetch 타입, LAZY (지연 로딩) EAGER(즉시 로딩)
//     */
//    @ManyToMany(fetch = FetchType.EAGER)
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role roles;
    private String profileImageUrl;

    @Builder
    public User(Long id, String name, String username, String password, String email, Boolean isActive, Boolean isNonLocked, Boolean isEmailEnabled, LocalDateTime lastLoginDate, Role roles, String profileImageUrl) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.password = password;
        this.email = email;
        this.isActive = isActive;
        this.isNonLocked = isNonLocked;
        this.isEmailEnabled = isEmailEnabled;
        this.lastLoginDate = lastLoginDate;
        this.roles = roles;
        this.profileImageUrl = profileImageUrl;
    }

    /**
     * 계정이 가지고 있는 권한을 리턴
     * @return Stream 인스턴트 객체로 반환.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        /*
        --- 생성하기 ( 배열(builder, 기본타입형)/ 컬렉션(generate, string)/ 빈 스트림(iterate, 파일 스트림) )
         - 스트림 인스턴스 생성
        stream(array)
        --- 가공하기 ( Filtering, Mapping, Sorting, Iterating)
         - 필터링 및 맵핑등 원하는 결과를 만들어가는 중간 작업
        SimpleGrantedAuthority란 GrantedAuthority 인터페이스를 베이스로 재구현해 권한을 저장한 객체
        map은 스트림 내 요소들의 특정조건에 해당하는 값으로 변환해준다.
        --- 결과 만들기 ( Calculating, Reduction, Collecting, Matching, Iterating )
         - 결과 만들기 : 최종적으로 결과를 만들어 내는 작업
        collect()는 Stream의 아이템들을 우리가 원하는 자료형으로 변환할 수 있다.

        전체 -> 맵핑 -> 필터링 1 -> 필터링 2 -> 결과 만들기 -> 결과물
         */
        List<GrantedAuthority> authorities = new ArrayList<>();

        authorities.add(new SimpleGrantedAuthority(this.roles.getKey()));
        return authorities;
//        return stream(this.user.getAuthorities())
//                .map(SimpleGrantedAuthority::new)
//                .collect(Collectors.toList());
    }

    /**
     * 계정의 패스워드를 리턴함
     * @return String 타입 리턴
     */
    @Override
    public String getPassword() {
        return this.password;
    }

    /**
     * 계정의 이름을 리턴
     * @return String 타입 리턴
     */
    @Override
    public String getUsername() {
        return this.username;
    }

    /**
     * 계정이 만료되지 않았는지 여부를 리턴
     * true : 만료되지 않음
     * false : 만료
     * @return boolean 타입으로 리턴
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 계정이 잠겨있는지 여부를 리턴
     * true : 잠겨있지 않음
     * false : 잠겨있음
     * @return boolean 타입으로 리턴
     */
    @Override
    public boolean isAccountNonLocked() {
        return this.isNonLocked;
    }

    /**
     * 계정의 패스워드가 만료된지 여부를 리턴
     * true : 만료되지 않음
     * false : 만료
     * @return boolean 타입으로 리턴
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 계정이 사용가능한지 여부를 리턴
     * true : 사용가능
     * false : 사용불가
     * @return boolean 타입으로 리턴
     */
    @Override
    public boolean isEnabled() {
        return this.isActive;
    }


    /*
    수정이 필요한 메서드.
     */

    public void roleEdit(Role role) {
        this.roles = role;
    }

    public void loginDateUpdate(LocalDateTime lastLoginDate){
        this.lastLoginDate = lastLoginDate;
    }

    public void userSecurityLockUpdate(boolean isActive, boolean isNotLocked, Boolean isEmailEnabled){
        this.isActive = isActive;
        this.isNonLocked = isNotLocked;
        this.isEmailEnabled = isEmailEnabled;
    }

    public void userProfileImageUpdate(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public void userPaswordReset(String password) {
        this.password = password;
    }
}
