package org.zerock.boardex.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

//댓글 객체
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReplyDTO {
    private Long rno;     //댓글 번호
    @NotNull
    private Long bno;     //게시글 번호
    @NotEmpty
    private String replyText;   //댓글
    @NotEmpty
    private String replyer;     //댓글작성자

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime regDate;  //댓글작성일
    //댓글수정일은 브라우저 화면에 안보임
    @JsonIgnore
    private LocalDateTime modDate;
}
