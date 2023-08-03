package org.zerock.boardex.repository;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.zerock.boardex.domain.Board;
import org.zerock.boardex.domain.Reply;

@SpringBootTest
@Log4j2
public class ReplyRepositoryTests {
    @Autowired
    private ReplyRepository replyRepository;
    @Test
    public void testInsert(){
        //게시글 번호
        Long bno=200L;
        //게시글의 번호를 가져와 board객체 생성
        Board board=Board.builder().bno(bno).build();
        //댓글의 정보를 가져와 reply객체 생성
        Reply reply=Reply.builder()
                .board(board)
                .replyText("댓글..21")
                .replyer("댓글작성자21")
                .build();
        //DB에 댓글 저장
        replyRepository.save(reply);
    }
    @Test
    public void testBoardReplies(){
        //게시글 번호
        Long bno=200L;
        //페이징(현재페이지:0, 페이지당글 개수:10, 댓글번호를 기준으로 내림차순 정렬
        Pageable pageable= PageRequest.of(0,10, Sort.by("rno").descending());
        //listOfBoard메서드 호출한 결과값을 result에 저장
        Page<Reply> result=replyRepository.listOfBoard(bno, pageable);
        //result의 결과값만큼 반복
        result.getContent().forEach(reply->{
            log.info(reply);
        });
    }
}
