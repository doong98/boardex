package org.zerock.boardex.domain;

import lombok.Getter;

//enum : 클래스처럼 보이게 하는 상수
//서로 관련 있는 상수들끼리 모아 상수들을 대표하는 이름으로 타입을 정의하는 것
//인덱스 있음. USER(0), ADMIN(1)
@Getter
public enum MemberRole {
    USER("ROLE_ADMIN"),
    ADMIN("ROLE_USER");
    MemberRole(String value){
        this.value=value;
    }
    private String value;
}
