package org.zerock.boardex.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.boardex.domain.Reply;

public interface ReplyRepository extends JpaRepository<Reply,Long> {
    //특정 게시글의 댓글도 페이징 처리
    @Query("select r from Reply r where r.board.bno = :bno")
    //객체<객체타입> : 제네릭(Generic) 문법
    Page<Reply> listOfBoard(@Param("bno") Long bno, Pageable pageable);
    //댓글이나 첨부파일 삭제
    void deleteByBoard_Bno(Long bno);
}
