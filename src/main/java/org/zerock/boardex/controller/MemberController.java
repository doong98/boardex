package org.zerock.boardex.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.boardex.dto.MemberJoinDTO;
import org.zerock.boardex.service.MemberService;
import org.zerock.boardex.service.MemberServiceImpl;

@Controller
@RequestMapping("/member")
@Log4j2
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    //로그인
    @GetMapping("/login")
    public void loginGET(String errorCode, String logout){
        log.info("로그인 화면");
    }
    //회원가입
    @GetMapping("/join")
    public void joinGET(){
        log.info("회원가입하기");
    }
    @PostMapping("/join")
    public String joinPOST(MemberJoinDTO memberJoinDTO, RedirectAttributes redirectAttributes){
        log.info("회원가입하기");
        log.info(memberJoinDTO);
        //회원이 아니면 가입이 되고, 회원이 이미 존재하면 다시 회원가입 페이지로 이동하기
        try {
            memberService.join(memberJoinDTO);
        } catch(MemberService.MidExistException e){
            redirectAttributes.addFlashAttribute("error","mid");
            return "redirect:/member/join";
        }
        //회원가입 후 이름:result, 값:success로 리다이렉트속성값 설정
        redirectAttributes.addFlashAttribute("result", "success");
        return "redirect:/";
    }
    //로그아웃
    @GetMapping("/logout")
    public String logout( String errorCode, String logout){
        return "redirect:/member/login?logout";
    }
}
