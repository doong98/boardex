package org.zerock.boardex.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class SampleController {
    class SampleDTO {
         private String p1, p2, p3;
         public String getP1() {
             return p1;
         }
        public String getP2() {
            return p2;
        }
        public String getP3() {
            return p3;
        }
   }


    @GetMapping("/hello")
    public void hello(Model model) {
        model.addAttribute("msg", "Hello Spring boot");
    }
    @GetMapping("/ex1")
    public void ex1(Model model) {
        List<String> list= Arrays.asList("돈가스","컵라면","우동","김밥");
        model.addAttribute("list",list);
    }
    @GetMapping("/ex2")
    public void ex2(Model model){
        //Stream이란? 시간이 지남에 따라 사용할 수 있게 되는 데이터 요소(파일을 읽거나 쓸 때, 통신할 때 사용함)
        //mapToObj()는 원시타입 Stream객체를 일반 Stream객체로 변환
        //IntStream.range(1,10) -> Data1, Data2, Data3...Data9
        List<String> strList= IntStream.range(1, 10)
                .mapToObj(i->"Data"+i)
                .collect(Collectors.toList());
        model.addAttribute("list", strList);
       //HashMap() : Map은 key와 value로 구성된 객체(Entity)임, 이 Map이 가진 인터페이스를 구현하는 메서드.
        Map<String, String> map=new HashMap();
        map.put("A","AAAA");
        map.put("B","BBBB");
        model.addAttribute("map",map);
        //SampleDTO클래스를 사용하여 sampleDTO인스턴스 객체를 생성함
        SampleDTO sampleDTO = new SampleDTO();
        //sampleDTO인스턴스 객체의 멤버변수에 문자열을 저장
        sampleDTO.p1="값 -- p1";
        sampleDTO.p2="값 -- p2";
        sampleDTO.p3="값 -- p3";
        model.addAttribute("dto", sampleDTO);
    }
    @GetMapping("/ex3")
    public void ex3(Model model){
        model.addAttribute("arr", new String[]{"AAA", "BBB", "CCC"});
    }
}
