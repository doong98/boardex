package org.zerock.boardex.service;

import org.zerock.boardex.dto.PageRequestDTO;
import org.zerock.boardex.dto.PageResponseDTO;
import org.zerock.boardex.dto.ReplyDTO;

public interface ReplyService {
    //댓글 등록
    Long register(ReplyDTO replyDTO);
    //댓글 조회
    ReplyDTO read(Long rno);
    //댓글 수정
    void modify(ReplyDTO replyDTO);
    //댓글 삭제
    void remove(Long rno);
    //특정 게시물의 댓글 목록 처리
    PageResponseDTO<ReplyDTO> getListOfBoard(Long bno, PageRequestDTO pageRequestDTO);
}
