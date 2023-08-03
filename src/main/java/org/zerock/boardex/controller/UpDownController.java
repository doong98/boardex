package org.zerock.boardex.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.boardex.dto.upload.UploadFileDTO;
import org.zerock.boardex.dto.upload.UploadResultDTO;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@RestController
@Log4j2
public class UpDownController {
    //application.properties에 작성한 파일 업로드 경로 가져옴
    @Value("${org.zerock.upload.path}")
    private String uploadPath;

    @Operation(summary="upload POST", description = "POST 방식으로 파일 등록")
    @PostMapping(value="/upload", consumes= "multipart/form-data")
    public List<UploadResultDTO> upload(UploadFileDTO uploadFileDTO, @Parameter(
            description = "Files to be uploaded",
            content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
    )
    @RequestPart(value = "files", required = true) MultipartFile[] multipartFiles)
            throws NoSuchAlgorithmException, IOException {

        log.info(uploadFileDTO);
        //업로드한 파일이 있다면
        if (uploadFileDTO.getFiles() != null){
            final List<UploadResultDTO> list=new ArrayList<>();

            //업로드한 파일 개수만큼 반복
            uploadFileDTO.getFiles().forEach(multipartFile->{
                //multipartFile의 정보에서 파일명을 얻어서 보여줌
                String originalName=multipartFile.getOriginalFilename();
                log.info(originalName);
                //같은 파일명일 때 처리
                //UUID : 네트워크에서 중복되지 않는 ID를 만들기 위한 표준규약
                String uuid= UUID.randomUUID().toString();
                //uploadPath + uuid + "_"+ 파일명 -> 새로운 경로 만들어서 savePath에 저장
                Path savePath= Paths.get(uploadPath, uuid+"_"+originalName);
                //업로드한 파일이 이미지인지 판단 여부
                boolean photo=false;

                try {
                    //실제 파일 저장
                    multipartFile.transferTo(savePath);
                    //업로드한 파일이 이미지라면 썸네일 보여줌
                    if(Files.probeContentType(savePath).startsWith("image")){
                        photo=true;
                        //uploadPath경로의 파일명으로 File객체를 만들어서 thumbFile에 저장
                        File thumbFile=new File(uploadPath, "s_"+uuid+"_"+originalName);
                        Thumbnailator.createThumbnail(savePath.toFile(), thumbFile, 200,200);
                    }
                } catch(IOException e) {
                    e.printStackTrace();
                }
                //파일업로드한 결과를 처리하기 위해 list에 UploadResultDTO객체 추가
                list.add(UploadResultDTO.builder()
                        .uuid(uuid)
                        .fileName(originalName)
                        .img(photo).build()
                );
            });
            return list;
        }
        return null;
    }
    //첨부파일 조회하기
    @Operation(summary = "view file", description = "GET방식으로 첨부파일 조회")
    @GetMapping("/view/{fileName}")
    public ResponseEntity<Resource> viewFileGET(@PathVariable String fileName){
        //c:/upload/c3cd930e-eaa9-4606-9967-cbba7854b8a8_flower.jpg 파일명을 가진 파일시스템리소스를 생성하여 resource에 저장
        Resource resource=new FileSystemResource(uploadPath+File.separator+fileName);
        //resource에서 파일명을 가져와서 resourceName에 문자열로 저장
        String resourceName=resource.getFilename();
        //HTTP 메시지의 구성 요소 중 하나로써, 클라이언트의 요청이나 서버의 응답에 포함되어 부가적인 정보를 HTTP 메시지에 포함할 수 있도록 해주는 객체
        HttpHeaders headers=new HttpHeaders();
        //Files.probeContentType() : 파일의 확장자(마임타입)을 가져오는 메서드임.
        try {
            headers.add("Content-Type", Files.probeContentType(resource.getFile().toPath()));
        } catch(Exception e){
            //내부서버에러라고 예외처리함.
            return ResponseEntity.internalServerError().build();
        }
        //OK 응답코드와 HttpHeaders정보와 resource(이미지파일) 반환
        return ResponseEntity.ok().headers(headers).body(resource);
    }
    //첨부파일 삭제
    @Operation(summary = "remove file", description = "DELETE 형식으로 첨부파일 삭제")
    @DeleteMapping("/remove/{fileName}")
    public Map<String, Boolean> removeFile(
            @PathVariable String fileName) {
        //c:/upload/c3cd930e-eaa9-4606-9967-cbba7854b8a8_flower.jpg 파일명을 가진 파일시스템리소스를 생성하여 resource에 저장
        Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);
        //리소스의 파일명을 가져와서 resourceName에 저장
        String resourceName = resource.getFilename();
        //총 개수가 정해지지 않은 배열 객체 생성
        Map<String, Boolean> resultMap = new HashMap<>();
        //파일 삭제 성공 여부 
        boolean removed = false;
        
        try {
            //파일의 확장자(종류)를 알아내서 contentType에 저장
            String contentType = Files.probeContentType(resource.getFile().toPath());
            //리소스 파일을 지우는데 성공하면 true를 removed에 저장
            removed = resource.getFile().delete();
            //만약 파일의 확장자가 image라면
            if (contentType.startsWith("image")) {
                //썸네일 객체를 생성한 다음 썸네일 파일을 저장
                //thumbnailFile=c:/upload/파일명.jpg
                File thumbnailFile = new File(uploadPath + File.separator + "s_" + fileName);
                //썸네일 삭제
                thumbnailFile.delete();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        //파일 삭제 여부를 resultMap 에 저장하여 반환
        resultMap.put("result", removed);
        return resultMap;
    }
}
