package org.zerock.boardex.repository;

import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Commit;
import org.zerock.boardex.domain.Board;
import org.zerock.boardex.domain.BoardImage;
import org.zerock.boardex.dto.BoardListAllDTO;
import org.zerock.boardex.dto.BoardListReplyCountDTO;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

@SpringBootTest
@Log4j2
public class BoardRepositoryTests {
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private ReplyRepository replyRepository;

    //데이터 입력하기
    @Test
    public void testInsert() {
        IntStream.range(1, 100).forEach(i -> {
            Board board = Board.builder()
                    .title("제목" + i)
                    .content("내용" + i)
                    .writer("사용자" + (i % 10))
                    .build();
            Board result = boardRepository.save(board);
            log.info("번호: "+result.getBno());
        });
    }
    //select기능
    @Test
    public void testSelect(){
        Long bno=10L;
        Optional<Board> result=boardRepository.findById(bno);
        //orElseThrow() :if ~ else를 사용해서 error처리해야 하는데 orElseThrow()를 쓰면 자동으로 error처리해줌, Optional에서 제공
        Board board=result.orElseThrow();
        log.info(board);
    }
    //update기능
    @Test
    public void testUpdate(){
        Long bno=99L;
        Optional<Board> result=boardRepository.findById(bno);
        Board board=result.orElseThrow();
        board.change("제목 수정 99", "내용 수정 99");
        boardRepository.save(board);
    }
    //삭제 기능
    @Test
    public void testDelete(){
        Long bno=2L;
        boardRepository.deleteById(bno);
    }
    //기본적인 페이징 처리 기능
    @Test
    public void testPaging(){
        //PageRequest.of() : page:현재 페이지, size:한 페이지에 보여줄 게시글 개수
        Pageable pageable= PageRequest.of(0,7, Sort.by("bno").descending());
        Page<Board> result=boardRepository.findAll(pageable);
        log.info("총 페이지 수:"+result.getTotalPages());
        log.info("총 게시글 수:"+result.getTotalElements());
        log.info("현재 페이지:"+result.getNumber());
        log.info("페이지 크기:"+result.getSize());
    }
    @Test
    public void testSearch(){
        Pageable pageable=PageRequest.of(1,10, Sort.by("bno").descending());
        boardRepository.search1(pageable);
    }
    @Test
    public void testSearchAll(){
        String[] types={"t","c","w"};
        String keyword="1";
        Pageable pageable=PageRequest.of(0,10, Sort.by("bno").descending());
        Page<Board> result=boardRepository.searchAll(types, keyword, pageable);
    }
    @Test
    public void testSearch2(){
        String[] types={"t","c","w"};
        String keyword="1";
        Pageable pageable=PageRequest.of(0,10, Sort.by("bno").descending());
        Page<Board> result=boardRepository.searchAll(types, keyword, pageable);
        //모든 페이지
        log.info(result.getTotalPages());
        //페이지 크기
        log.info(result.getSize());
        //페이지 번호
        log.info(result.getNumber());
        //이전 페이지와 다음 페이지
        log.info(result.hasPrevious()+": "+result.hasNext());
        result.getContent().forEach(board->log.info(board));
    }
    @Test
    public void testSearchReplyCount() {
        String[] types={"t","c","w"};
        String keyword="1";
        Pageable pageable=PageRequest.of(1,10, Sort.by("bno").descending());
        Page<BoardListReplyCountDTO> result=boardRepository.searchWithReplyCount(types, keyword, pageable);
        //전체 페이지
        log.info(result.getTotalPages());
        log.info(result.getSize());
        log.info(result.getNumber());
        log.info(result.hasPrevious()+": "+result.hasNext());
        result.getContent().forEach(board->log.info(board));

    }
    @Test
    public void testInsertWithImages(){
        Board board=Board.builder()
                .title("이미지 테스트1")
                .content("첨부파일 테스트1")
                .writer("작성자1")
                .build();
        for(int i=0; i<3; i++){
            board.addImage(UUID.randomUUID().toString(), "file"+i+".jpg");
        }
        boardRepository.save(board);
    }
    //이미지 로딩(게시물 조회) 테스트
    //@Transactional : 필요할 때마다 메서드 내에서 추가적인 쿼리를 여러번 실행할 수 있다.
    @Test
    @Transactional
    public void testReadWithImages(){
        Optional<Board> result=boardRepository.findByIdWithImages(1L);
        Board board=result.orElseThrow();
        log.info(board);
        log.info("===================");
        for(BoardImage boardImage : board.getImageSet()){
            log.info(board.getImageSet());
        }
    }
    //첨부파일 수정하기
    @Test
    @Transactional
    @Commit
    public void testModifyImages(){
        //boardRepository의 findByIdWithImages메서드 호출
        Optional<Board> result=boardRepository.findByIdWithImages(2L);
        //게시글 번호(bno)에 해당하는 데이터를 찾아서 board객체에 저장
        Board board=result.orElseThrow();
        //board객체의 첨부파일(이미지) 삭제
        board.clearImage();
        //수정된 이미지로 변경
        for(int i=0; i<3; i++){
            board.addImage(UUID.randomUUID().toString(), "updateFile"+i+".jpg");
        }
        boardRepository.save(board);
    }
    //먼저 Reply 엔티티들을 삭제한 다음 Board를 삭제함
    @Test
    @Transactional
    @Commit
    public void testRemoveAll(){
        Long bno = 2L;
        //댓글이나 첨부파일 등을 모두 삭제
        replyRepository.deleteByBoard_Bno(bno);
        //게시글 삭제
        boardRepository.deleteById(bno);
    }
    //게시판에 게시글 데이터, 첨부파일 입력하기
    @Test
    public void testInsertAll(){
        for(int i=1; i<=100; i++){
            Board board=Board.builder()
                    .title("제목 테스트" + i)
                    .content("내용 테스트" + i)
                    .writer("작성자" + i)
                    .build();
            //게시글 1개당 첨부파일 3개 추가
            for(int j=0; j<3; j++){
                //게시글 번호가 5의 배수이면 첨부파일 없음.
                if(i % 5 == 0){
                    //i값을 5로 나눈 나머지가 0이면 for문 빠져나감
                    continue;
                }
                board.addImage(UUID.randomUUID().toString(), i+"file"+j+".jpg");
            }
            boardRepository.save(board);
        }
    }
    @Test
    @Transactional
    public void testSearchImageReplyCount(){
        //페이징(현재 페이지 0번, 페이지 크기 10으로 페이징)한 객체를 게시글 번호로 내림차순하여 pageable에 저장
        Pageable pageable=PageRequest.of(0,10, Sort.by("bno").descending());
        //boardRepository.searchWithAll(null, null, pageable);
        
        //게시글, 댓글, 첨부파일을 모두 검색하여 페이징 한 다음 result에 저장
        Page<BoardListAllDTO> result=boardRepository.searchWithAll(null,null,pageable);
        log.info("-------------------------------");
        log.info(result.getTotalElements());
        result.getContent().forEach(boardListAllDTO -> log.info(boardListAllDTO));

    }


}
