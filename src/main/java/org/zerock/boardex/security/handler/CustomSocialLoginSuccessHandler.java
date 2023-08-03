package org.zerock.boardex.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.zerock.boardex.dto.MemberSecurityDTO;

import java.io.IOException;

//AuthenticationSuccessHandler : 로그인 성공 후 특정 동작을 제어하기 위한 인터페이스
@Log4j2
@RequiredArgsConstructor
public class CustomSocialLoginSuccessHandler implements AuthenticationSuccessHandler {
    //비밀번호를 암호화하는 객체
    private final PasswordEncoder passwordEncoder;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("--------------------------");
        log.info("카카오 로그인 핸들러 .....");
        //getPrincipal() : 로그인한 객체의 사용자 정보를 가져옴
        log.info(authentication.getPrincipal());
        MemberSecurityDTO memberSecurityDTO=(MemberSecurityDTO) authentication.getPrincipal();
        String encodedPw=memberSecurityDTO.getMpw();
        //소셜 로그인(카카오 로그인)이고 회원의 비밀번호가 1111
        if(memberSecurityDTO.isSocial() && (memberSecurityDTO.getMpw().equals("1111") || passwordEncoder.matches("1111", memberSecurityDTO.getMid()))) {
            log.info("비밀번호를 변경해야 합니다");
            log.info("회원 정보를 수정하여 다시 접속합니다");
            response.sendRedirect("/member/modify");
            return;
        }else{
            response.sendRedirect("/board/list");
        }
    }
}
