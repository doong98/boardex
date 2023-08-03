package org.zerock.boardex.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.zerock.boardex.dto.PageRequestDTO;
import org.zerock.boardex.dto.PageResponseDTO;
import org.zerock.boardex.dto.ReplyDTO;
import org.zerock.boardex.service.ReplyService;
import java.util.HashMap;
import java.util.Map;

@Tag(name="reply", description = "댓글 작성")
@RestController
@RequestMapping("/replies")
@Log4j2
//swagger ui를 이용하여 필요한 기능을 개발하기 위한 의존성 주입
@RequiredArgsConstructor
public class ReplyController {
    private final ReplyService replyService;

    //@Operation(summary="Replies POST", description="POST 방식으로 댓글 등록")
    //consumes : 특정 타입의 데이터를 담고 있는 요청만 처리하는 핸들러
    @PostMapping(value="/", consumes= MediaType.APPLICATION_JSON_VALUE)
    //Map(배열 개수가 정해져 있지 않을 때 사용)
    //Map<String, Long> : key의 자료형, value의 자료형
    //ResponseEntity<객체> : 일반적인 객체 외에 다양한 객체를 담아서 전달가능
    public Map<String, Long> register(@Valid @RequestBody ReplyDTO replyDTO, BindingResult bindingResult) throws BindException {
        log.info(replyDTO);
        //만약 bindingResult에서 에러가 있다면 BindException 예외처리
        if(bindingResult.hasErrors()){
            //BindException : 입력 값 검증
            throw new BindException(bindingResult);
        }
        //키:rno, 값:111 형태로 resultMap에 값 저장
        Map<String, Long> resultMap = new HashMap<>();
        Long rno = replyService.register(replyDTO);
        resultMap.put("rno",rno);
        return resultMap;
    }
    //댓글 목록 처리
    @Operation(summary="게시글의 댓글 목록", description = "GET방식으로 특정 게시물의 댓글 목록")
    @GetMapping(value="/list/{bno}")
    public PageResponseDTO<ReplyDTO> getList(
            //@PathVariable("bno") : ?bno=197 와 같은 쿼리 스트링의 값을 전달받음
            @PathVariable("bno") Long bno,
            //페이징 객체
            PageRequestDTO pageRequestDTO
    ){
        //bno번호에 해당하는 게시글의 댓글 목록을 페이징해서 responseDTO에 저장
        PageResponseDTO<ReplyDTO> responseDTO=replyService.getListOfBoard(bno, pageRequestDTO);
        //특정 게시물의 댓글 목록 반환
        return responseDTO;
    }
    //특정 댓글 조회
    @Operation(summary="특정 댓글 조회", description = "GET 방식으로 특정 댓글 조회")
    @GetMapping("/{rno}")
    public ReplyDTO getReplyDTO(
            //@PathVariable("rno") : ?rno=10 와 같은 쿼리 스트링의 값을 전달받음
            @PathVariable("rno") Long rno){
        //특정 댓글을 찾아서 replyDTO에 저장
        ReplyDTO replyDTO=replyService.read(rno);
        //특정 댓글을 반환
        return replyDTO;
    }
    //댓글 삭제
    @Operation(summary = "댓글 삭제", description = "DELETE 방식으로 특정 댓글 삭제")
    @DeleteMapping("/{rno}")
    public Map<String, Long> remove(
            @PathVariable("rno") Long rno){
        replyService.remove(rno);
        Map<String, Long> resultMap=new HashMap<>();
        resultMap.put("rno", rno);
        return resultMap;
    }
    @Operation(summary = "특정 댓글 수정", description = "PUT 방식으로 특정 댓글 수정")
    //consumes=MediaType.APPLICATION_JSON_VALUE : 클라이언트가 서버에게 보내는 데이터 타입을 명시함(JSON형식)
    @PutMapping(value="/{rno}", consumes=MediaType.APPLICATION_JSON_VALUE)
    
    public Map<String, Long> modify(
            // ?rno=10 과 같은 쿼리 스트링 만듬
            @PathVariable("rno") Long rno,
            //xml이나 json기반의 메시지를 요청할 때 사용
            @RequestBody ReplyDTO replyDTO){
        //댓글 번호를 일치시킴
        replyDTO.setRno(rno);
        //replyService의 modify메서드 호출
        replyService.modify(replyDTO);
        //resultMap 객체 생성
        Map<String, Long> resultMap=new HashMap<>();
        //resultMap에 댓글 번호로 수정한 댓글 저장
        resultMap.put("rno", rno);
        //수정한 댓글 결과를 반환
        return resultMap;
    }
}
