package org.zerock.boardex.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zerock.boardex.domain.Board;
import org.zerock.boardex.dto.*;
import org.zerock.boardex.repository.BoardRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest
@Log4j2
public class BoardServiceTests {
    @Autowired
    private BoardService boardService;
    @Test   //데이터 등록 작업 테스트
    public void testRegister(){
        log.info(boardService.getClass().getName());
        BoardDTO boardDTO=BoardDTO.builder()
                .title("테스트 예제 0612 ")
                .content("테스트 내용 0612 ")
                .writer("강감찬")
                .build();
        Long bno=boardService.register(boardDTO);
        log.info("번호:"+bno);
    }
    //데이터 수정 작업
    @Test
    public void testModify(){
        BoardDTO boardDTO=BoardDTO.builder()
                .bno(101L)
                .title("제목 수정 101")
                .content("내용 수정 101")
                .build();
        boardService.modify(boardDTO);
    }
    //데이터 삭제 작업
    @Test
    public void testRemove(){
        Long bno=199L;
        boardService.remove(bno);
    }
    //목록과 검색 기능
    @Test
    public void testList(){
        PageRequestDTO pageRequestDTO=PageRequestDTO.builder()
                .type("tcw")
                .keyword("1")
                .page(1)
                .size(10)
                .build();
        PageResponseDTO<BoardDTO> responseDTO=boardService.list(pageRequestDTO);
        log.info(responseDTO);
    }
    //데이터 등록 기능 테스트
    @Test
    public void testRegisterWithImages(){
        log.info(boardService.getClass().getName());

        BoardDTO boardDTO=BoardDTO.builder()
                .title("파일")
                .content("내용입니다.")
                .writer("나야나")
                .build();
        boardDTO.setFileNames(
                Arrays.asList(
                        UUID.randomUUID()+"_aaa.jpg",
                        UUID.randomUUID()+"_bbb.jpg",
                        UUID.randomUUID()+"_ccc.jpg"
                ));
        Long bno=boardService.register(boardDTO);
        log.info("bno : "+bno);
    }
    //데이터 조회할 때 게시글과 첨부파일 같이 처리하는지 테스트
    @Test
    public void testReadAll(){
        Long bno=5L;
        BoardDTO boardDTO=boardService.readOne(bno);
        log.info(boardDTO);
        for(String fileName : boardDTO.getFileNames()){
            log.info(fileName);
        }
    }
    //게시물 수정(첨부파일 포함)
    @Test
    public void testModify2(){
        BoardDTO boardDTO=BoardDTO.builder()
                .bno(1L)
                .title("제목 수정하기99")
                .content("내용 수정하기99")
                .build();
        //첨부파일 한개 추가하기
        boardDTO.setFileNames(Arrays.asList(UUID.randomUUID()+"_abcd.jpg"));
        boardService.modify(boardDTO);
    }
    //게시물 삭제
    @Test
    public void testRemoveAll(){
        Long bno=101L;
        boardService.remove(bno);
    }
    @Test
    public void testListWithAll(){
        //1번 페이지의 글 목록 10개만 가져와서 pageRequestDTO에 저장
        PageRequestDTO pageRequestDTO=PageRequestDTO.builder()
                .page(1)
                .size(10)
                .build();
        //페이지 객체안에 게시글 목록 포함해서 boardService의 listWithAll메서드 호출한 다음 실행결과를 responseDTO에 저장
        PageResponseDTO<BoardListAllDTO> responseDTO=boardService.listWithAll(pageRequestDTO);
        //responseDTO의 getDtoList() 메서드 호출한 결과를 dtoList에 저장(첨부파일을 포함한 게시글 목록)
        List<BoardListAllDTO> dtoList=responseDTO.getDtoList();
        //페이지크기만큼 반복
        dtoList.forEach(boardListAllDTO -> {
            //게시글 번호와 제목을 기록
            log.info(boardListAllDTO.getBno()+":"+boardListAllDTO.getTitle());
            //첨부파일 이미지가 있으면 그 이미지 수만큼 반복
            if(boardListAllDTO.getBoardImages() != null){
                for(BoardImageDTO boardImage : boardListAllDTO.getBoardImages()) {
                    log.info(boardImage);
                }
            }
            log.info("---------------------------");
        });
    }
}
