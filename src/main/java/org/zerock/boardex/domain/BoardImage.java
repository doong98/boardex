package org.zerock.boardex.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude="board")
//Comparable() 객체 : 비교해야 하는 객체에 적용함. 주로 정렬을 위해서 사용(파일명 순서대로 오름차순 정렬)
public class BoardImage implements Comparable<BoardImage>{
    @Id
    private String uuid;
    private String fileName;
    private int ord;
    @ManyToOne
    private Board board;
    @Override
    public int compareTo(BoardImage other){
        return this.ord-other.ord;
    }
    public void changeBoard(Board board){
        this.board=board;
    }
}
