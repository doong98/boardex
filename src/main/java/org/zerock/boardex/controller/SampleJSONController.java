package org.zerock.boardex.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
//@RestController : 자바의 인스턴스를 JSON형식으로 변환해서 출력해 줌
@RestController
public class SampleJSONController {
    @GetMapping("/helloArr")
    public String[] helloArr(){
        return new String[]{"AAA","BBB","CCC"};
    }
}
