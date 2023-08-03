package org.zerock.boardex.repository;

import jakarta.persistence.Entity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.boardex.domain.Board;
import org.zerock.boardex.repository.search.BoardSearch;

import java.util.Optional;

//Mapper처럼 데이터베이스 관련 작업 처리(CRUD와 페이징 처리)
public interface BoardRepository extends JpaRepository<Board, Long>, BoardSearch {
    @Query(value="select now()", nativeQuery=true)
    String getTime();
    //첨부파일 수정
    @EntityGraph(attributePaths = {"imageSet"})
    @Query("select b from Board b where b.bno =:bno")
    Optional<Board> findByIdWithImages(@Param("bno") Long bno);

    
    
}
