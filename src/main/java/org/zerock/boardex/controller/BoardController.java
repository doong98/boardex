package org.zerock.boardex.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.boardex.dto.*;
import org.zerock.boardex.service.BoardService;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

@Controller
@RequestMapping("/board")
@Log4j2
@RequiredArgsConstructor
public class BoardController {

    //첨부파일의 경로 주입받기(c:/upload)
    @Value("${org.zerock.upload.path}")
    private String uploadPath;

    private final BoardService boardService;
    @GetMapping("/list")
    public void list(PageRequestDTO pageRequestDTO, Model model){
        PageResponseDTO<BoardListAllDTO> responseDTO=boardService.listWithAll(pageRequestDTO);
        log.info(responseDTO);
        model.addAttribute("responseDTO", responseDTO);
    }
    //등록 화면 처리
    @GetMapping("/register")
    public void registerGET(){

    }
    //Validation이란 : 결과가 null값일 때 null pointer exception이 발생함, 이런 부분을 방지하기 위해 검증하는 과정
    //BindingResult : 스프링이 제공하는 검증 오류를 보관하는 객체
    //데이터 유효성 검사에서 에러가 발생하면 그 에러 정보를 저장함
    //RedirectAttributes : 서버에 데이터를 전송하면 Redirect발생함. 원래 Redirect는 GET방식(데이터 얻어옴)이라 데이터 전송에는 적합하지 않으나, 스프링 부트의 RedirectAttributes를 사용하여 GET방식으로도 데이터를 서버에 전송할 수 있다.
    //addFlashAttribute : 서버에 데이터를 POST방식으로 전달하는 속성.
    @PreAuthorize("hasRole('USER')")  //특정사용자만 권한설정
    @PostMapping("/register")
    public String registerPost(@Valid BoardDTO boardDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes){
        log.info("게시글 작성하기");
        if(bindingResult.hasErrors()){
            log.info("에러 있음.");
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            return "redirect:/board/register";
        }
        log.info(boardDTO);
        Long bno=boardService.register(boardDTO);
        redirectAttributes.addFlashAttribute("result", bno);
        return "redirect:/board/list";
    }
    //조회 화면
    //로그인한 사용자만 게시글 조회
    @PreAuthorize("isAuthenticated()")
    @GetMapping({"/read","/modify"})
    public void read(Long bno, PageRequestDTO pageRequestDTO, Model model){
        //boardService의 readOne메서드를 호출하면서 bno(번호)값을 전달하면 readOne메서드 실행결과가 boardDTO로 반환됨.
        BoardDTO boardDTO=boardService.readOne(bno);
        log.info(boardDTO);
        //model객체가 boardDTO(value)를 dto라는 이름으로 사용함.
        model.addAttribute("dto", boardDTO);
    }
    //게시글 수정하기
    @PreAuthorize("principal.username == #boardDTO.writer")
    @PostMapping("/modify")
    public String modify(PageRequestDTO pageRequestDTO, @Valid BoardDTO boardDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes){
        log.info("게시판 수정하기"+boardDTO);
        //만약 bindingResult에 에러가 있다면
        if(bindingResult.hasErrors()) {
            //문자열 기록
            log.info("에러 있어요..");
            //pageRequestDTO 객체의 getLink() 메서드 호출한 결과를 link에 저장, link="page=3&size=10&type=tcw&keyword=자바"
            String link=pageRequestDTO.getLink();
            //bindingResult객체의 모든 에러를 "errors"라는 이름으로 POST방식으로 서버에 전송->[[${errors}]]
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            //데이터의 번호(bno)를 bno라는 이름으로 서버에 POST방식으로 전송
            redirectAttributes.addAttribute("bno", boardDTO.getBno());
            //쿼리스트링 : /board/modify?page=3&size=10&type=tcw&keyword=자바 -> 이 주소로 이동함.
            return "redirect:/board/modify?"+link;
        }
        //boardService의 modify함수 호출하면서 boardDTO객체 넘김
        boardService.modify(boardDTO);
        //데이터 이름은 result,값은 modified로 서버에 POST방식으로 데이터 전달
        redirectAttributes.addFlashAttribute("result","modified");
        //수정된 데이터의 번호를 bno라는 이름으로 서버에 전달
        redirectAttributes.addAttribute("bno", boardDTO.getBno());
        // board/read로 이동
        return "redirect:/board/read";
    }
    //삭제처리
    @PreAuthorize("principal.username == #boardDTO.writer")
    @PostMapping("/remove")
    public String remove(BoardDTO boardDTO, RedirectAttributes redirectAttributes){
        Long bno=boardDTO.getBno();
        log.info("게시글 삭제하기"+bno);
        //boardService객체의 remove메서드 호출하면서 bno(번호)를 넘김
        boardService.remove(bno);
        //게시글이 데이터베이스에서 지워지면 첨부파일도 삭제
        log.info(boardDTO.getFileNames());
        List<String> fileNames=boardDTO.getFileNames();
        if(fileNames != null && fileNames.size() > 0) {
            removeFiles(fileNames);
        }

        //이름은 result로, 값은 removed로 서버에 POST방식으로 전달
        redirectAttributes.addFlashAttribute("result", "removed");
        // board/list로 이동, 정상 사용자가 아니면 로그인 화면으로 이동
        return "redirect:/board/list";
    }
    public void removeFiles(List<String> files){
        for(String fileName:files){
            Resource resource=new FileSystemResource(uploadPath+ File.separator+fileName);
            String resourceName=resource.getFilename();
            try{
                String contentType= Files.probeContentType(resource.getFile().toPath());
                resource.getFile().delete();
                //썸네일이 존재하면 썸네일도 삭제
                if(contentType.startsWith("image")) {
                    File thumbnailFile=new File(uploadPath+File.separator+"s_"+fileName);
                    thumbnailFile.delete();
                }
            } catch (Exception e){
                log.error(e.getMessage());
            }
        }
    }
}
