package org.zerock.boardex.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
//프로젝트 시작페이지
@Controller
public class HomeController {
    @GetMapping("/")
    public String home(){
        return "home";
    }
}
