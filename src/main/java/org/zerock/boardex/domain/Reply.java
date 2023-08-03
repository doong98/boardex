package org.zerock.boardex.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
//쿼리의 조건으로 자주 사용되는 컬럼에는 인덱스 설정함.
@Table ( name="Reply", indexes={@Index(name="idx_reply_bno", columnList = "board_bno")})
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude="board")
//BaseEntity : 작성일, 수정일 상속 받음
public class Reply extends BaseEntity {
    //Primary key 설정, 필수요소, 자동으로 번호가 생성됨(auto_increment)
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long rno;  //댓글번호

    //다대일 관계(게시글 하나에 여러 개의 댓글 생성 가능)
    //ManyToOne작성할 때는 반드시 FetchType.LAZY로 설정해야 함
    @ManyToOne(fetch = FetchType.LAZY)
    private Board board;    //게시판 객체
    private String replyText;  //댓글
    private String replyer;    //댓글작성자

    //댓글내용만 수정가능
    //외부에서 text내용을 전달받아서 replyText에 저장
    public void changeText(String text){
        this.replyText=text;
    }
}
