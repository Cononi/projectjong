package com.winesee.projectjong.domain.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @author Jhong
 * @version 1.0
 * @since 2022-02-14
 * 유저 Role
 * 권한 관리.
 */
@Getter
@RequiredArgsConstructor
public enum Role {

    USER("ROLE_USER", "와린이"),
    STAR("ROLE_STAR", "와스터"),
    ADMIN("ROLE_ADMIN", "운영자");

    private final String key;
    private final String title;
}
