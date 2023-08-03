package org.zerock.boardex.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.zerock.boardex.domain.Board;
import org.zerock.boardex.dto.*;
import org.zerock.boardex.repository.BoardRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class BoardServiceImpl implements BoardService{
    private final ModelMapper modelMapper;
    private final BoardRepository boardRepository;
    //데이터 등록 작업
    @Override
    public Long register(BoardDTO boardDTO){
        //게시판의 내용(데이터)를 전송하는 객체 boardDTO와 Board객체를 매핑한 결과를 board객체에 저장
        Board board=dtoToEntity(boardDTO);
        //boardRepository.save(board) : 데이터베이스에 board결과를 최종적으로 저장
        //getBno() : 데이터베이스에서 글번호를 찾아서 bno변수에 저장
        Long bno=boardRepository.save(board).getBno();
        //검색한 글번호를 반환함.
        return bno;
    }
    //데이터 조회 작업
    @Override
    public BoardDTO readOne(Long bno){
        //board_image까지 조인 처리하는 findByWithImages() 사용
        Optional<Board> result=boardRepository.findByIdWithImages(bno);
        //orElseThrow() : result값이 null이면 예외처리함.
        Board board=result.orElseThrow();
        //board객체와 BoardDTO객체 매핑한 결과(첨부파일과 댓글 포함)를 boardDTO에 저장
        BoardDTO boardDTO=entityToDTO(board);
        //boardDTO 객체를 반환함.
        return boardDTO;
    }
    //데이터 수정 작업
    @Override
    public void modify(BoardDTO boardDTO){
        //데이터베이스에서 글번호로 데이터 검색한 결과를 result에 저장
        Optional<Board> result=boardRepository.findById(boardDTO.getBno());
        //result 결과가 null이면 예외처리함.
        Board board=result.orElseThrow();
        //board(result)의 제목, 글내용을 수정해서 다시 board에 저장
        board.change(boardDTO.getTitle(), boardDTO.getContent());
        //첨부파일도 처리함
        board.clearImage();
        if(boardDTO.getFileNames() != null){
            for(String fileName : boardDTO.getFileNames()){
                String[] arr=fileName.split("_");
                board.addImage(arr[0], arr[1]);
            }
        }
        //board의 수정된 결과를 데이터베이스에 저장
        boardRepository.save(board);
    }
    //데이터 삭제 작업
    @Override
    public void remove(Long bno){
        //글번호를 검색하여 글번호에 해당하는 데이터 삭제
        boardRepository.deleteById(bno);
    }
    @Override
    public PageResponseDTO<BoardDTO> list(PageRequestDTO pageRequestDTO){
        //pageRequestDTO객체에서 type을 얻어와서 types에 저장(배열)t,c,w
        String[] types=pageRequestDTO.getTypes();
        //pageRequestDTO객체에서 키워드 얻어와서 keyword에 저장
        String keyword=pageRequestDTO.getKeyword();
        //글번호를 매개변수로 넘기면서 pageRequestDTO객체의 getPageable메서드 호출한 다음 그 결과값을 pageable에 저장
        Pageable pageable=pageRequestDTO.getPageable("bno");
        //데이터베이스에서 types, keyword,pageable에 해당하는 목록을 모두 찾아서 그 결과를 result에 저장
        Page<Board> result=boardRepository.searchAll(types, keyword, pageable);
        //검색결과(result)의 내용을 다시한번 board에 매핑하고 그 매핑결과를 합쳐서 dtoList에 저장
        List<BoardDTO> dtoList=result.getContent().stream()
                .map(board->modelMapper.map(board, BoardDTO.class))
                .collect(Collectors.toList());
        //types, keyword, pageable 로 매핑한 결과 목록을 화면에 보여주기 위한 값 반환
        return PageResponseDTO.<BoardDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int)result.getTotalElements())
                .build();
    }
    @Override
    //PageResponseDTO<T> : Object타입임.
    public PageResponseDTO<BoardListReplyCountDTO> listWithReplyCount(PageRequestDTO pageRequestDTO){
        //검색 기준(t, c, w) 값을 가져와서 types에 배열로 저장
        String[] types=pageRequestDTO.getTypes();
        //검색 키워드 가져와서 keyword에 문자열로 저장
        String keyword=pageRequestDTO.getKeyword();
        //게시글 번호를 기준으로 페이징 된 객체를 pageable에 저장
        Pageable pageable=pageRequestDTO.getPageable("bno");
        //게시글의 댓글 개수가 포함된 게시판 정보를 result에 저장
        Page<BoardListReplyCountDTO> result=boardRepository.searchWithReplyCount(types, keyword, pageable);
        //페이징 정보와 검색결과(리스트)와 총 목록 개수등의 정보를 반환
        return PageResponseDTO.<BoardListReplyCountDTO>withAll()
                .pageRequestDTO(pageRequestDTO)   //paging
                .dtoList(result.getContent())     //게시판 목록
                .total((int)result.getTotalElements())  //게시판 목록 총 개수
                .build();
    }
    @Override
    public PageResponseDTO<BoardListAllDTO> listWithAll(PageRequestDTO pageRequestDTO) {
        String[] types=pageRequestDTO.getTypes();
        String keyword=pageRequestDTO.getKeyword();
        Pageable pageable=pageRequestDTO.getPageable("bno");

        Page<BoardListAllDTO> result=boardRepository.searchWithAll(types,keyword, pageable);

        return PageResponseDTO.<BoardListAllDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(result.getContent())
                .total((int)result.getTotalElements())
                .build();
    }
}
