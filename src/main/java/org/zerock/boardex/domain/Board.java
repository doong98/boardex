package org.zerock.boardex.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "imageSet")
public class Board extends BaseEntity {
    //DB에 게시글이 추가될 때 자동 생성되는 번호(auto_increment)
    @Id
    //키 생성 전략 중 하나임 - IDENTITY(DB에서 알아서 번호 매김)
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long bno;   //글번호

    @Column(length=500, nullable=false)
    private String title;    //글제목

    @Column(length = 2000, nullable=false)
    private String content;   //내용

    @Column(length = 50, nullable = false)
    private String writer;    //글쓴이

    //OneToMany는 기본적으로 각 Entity에 해당하는 테이블을 독립적으로 생성하고 중간에 매핑해주는 테이블도 생성함.
    //mappedBy : BoardImage의 board변수 지정
    @OneToMany(mappedBy="board",
            cascade={CascadeType.ALL},
            fetch=FetchType.LAZY,
            orphanRemoval = true)
    @Builder.Default
    @BatchSize(size=20)   //20개씩 모아서 처리
    private Set<BoardImage> imageSet=new HashSet<>();
    //댓글
    @OneToMany(mappedBy="board", fetch=FetchType.EAGER, cascade=CascadeType.REMOVE)
    private List<Reply> reply;

    public void addImage(String uuid, String fileName){
        BoardImage boardImage=BoardImage.builder()
                .uuid(uuid)
                .fileName(fileName)
                .board(this)
                .ord(imageSet.size())
                .build();
        imageSet.add(boardImage);
    }
    //첨부파일 삭제
    public void clearImage(){
        imageSet.forEach(boardImage-> boardImage.changeBoard(null));
        this.imageSet.clear();
    }

    //수정기능
    public void change(String title, String content){
        this.title=title;
        this.content=content;
    }

}
