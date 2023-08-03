package org.zerock.boardex.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.zerock.boardex.dto.MemberJoinDTO;

public interface MemberService {
    //회원이 이미 존재하는 경우 예외처리함.
    static class MidExistException extends Exception {

    }
    void join(MemberJoinDTO memberJoinDTO) throws MidExistException ;
    UserDetails loadUserByUsername(String mid);
}
