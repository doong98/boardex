package org.zerock.boardex.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
//@MappedSuperclass : 해당 어노테이션이 적용된 클래스는 테이블로 생성되지 않는다. 이 클래스를 상속받은 엔티티에 매핑되는 테이블에 regDate와 modDate가 생성된다. (부모 클래스로만 사용된다. Entity 클래스가 아님)
@MappedSuperclass
@EntityListeners(value={ AuditingEntityListener.class})
@Getter
//abstract : 추상클래스(재정의를 위해 만들어진 객체)
abstract class BaseEntity {
    @CreatedDate
    @Column(name="regdate", updatable=false)
    private LocalDateTime regDate;  //글 생성 날짜와 시간

    @LastModifiedDate
    @Column(name="moddate")
    private LocalDateTime modDate;   //글 수정 날짜와 시간

}
