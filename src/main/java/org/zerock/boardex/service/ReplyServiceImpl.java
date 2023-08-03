package org.zerock.boardex.service;

import groovy.util.logging.Log4j2;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.zerock.boardex.domain.Reply;
import org.zerock.boardex.dto.PageRequestDTO;
import org.zerock.boardex.dto.PageResponseDTO;
import org.zerock.boardex.dto.ReplyDTO;
import org.zerock.boardex.repository.ReplyRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class ReplyServiceImpl implements ReplyService {
    //static : 정적인 변수(객체) 정의할 때 사용, 프로그램이 실행될 때 만들어져서 프로그램이 끝날 때까지 없어지지 않고 작동되다가 프로그램이 끝나면 사라지는 변수
    //final : 최종적으로 데이터를 갖고 있는 변수
    private final ReplyRepository replyRepository;
    private final ModelMapper modelMapper;
    //댓글 입력하기
    @Override
    public Long register(ReplyDTO replyDTO) {
        //클래스 객체(Reply)와 데이터 전송 객체(ReplyDTO) 매핑한 결과를 reply에 저장
        Reply reply=modelMapper.map(replyDTO, Reply.class);
        //댓글이 DB에 저장되면 그 댓글의 번호를 가져와서 rno에 저장
        Long rno=replyRepository.save(reply).getRno();
        //댓글번호 반환
        return rno;
    }
    //댓글 조회
    @Override
    public ReplyDTO read(Long rno){
        Optional<Reply> replyOptional=replyRepository.findById(rno);
        Reply reply=replyOptional.orElseThrow();
        return modelMapper.map(reply, ReplyDTO.class);
    }
    //댓글 수정
    @Override
    public void modify(ReplyDTO replyDTO){
        Optional<Reply> replyOptional=replyRepository.findById(replyDTO.getRno());
        Reply reply=replyOptional.orElseThrow();
        reply.changeText(replyDTO.getReplyText());
        replyRepository.save(reply);
    }
    //댓글 삭제
    @Override
    public void remove(Long rno){
        replyRepository.deleteById(rno);
    }
    //특정 게시물의 댓글 목록 처리
    @Override
    public PageResponseDTO<ReplyDTO> getListOfBoard(Long bno, PageRequestDTO pageRequestDTO) {
        //댓글 페이징 객체
        //페이지 번호가 0 이하이면 0, 0보다 크면 현재 페이지 번호
        //getPage()-1 : 페이지 번호는 0부터 시작함.
        Pageable pageable= PageRequest.of(pageRequestDTO.getPage() <= 0? 0:pageRequestDTO.getPage() -1, pageRequestDTO.getSize(), Sort.by("rno").ascending());
        //게시글 번호에 해당하는 댓글을 페이징한 결과를 result에 저장
        Page<Reply> result=replyRepository.listOfBoard(bno, pageable);
        //result(댓글)의 내용을 ReplyDTO객체와 매핑한 결과를 리스트 형태로 dtoList에 저장
        List<ReplyDTO>  dtoList=result.getContent().stream().map(reply->modelMapper.map(reply,ReplyDTO.class)).collect(Collectors.toList());
        //게시글과 댓글 모든 정보 취합하여 반환함
        return PageResponseDTO.<ReplyDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int)result.getTotalElements())
                .build();
    }
}
