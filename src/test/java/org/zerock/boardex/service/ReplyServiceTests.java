package org.zerock.boardex.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zerock.boardex.dto.ReplyDTO;

@SpringBootTest
@Log4j2
public class ReplyServiceTests {
    @Autowired
    private ReplyService replyService;
    @Test
    public void testRegister(){
        ReplyDTO replyDTO=ReplyDTO.builder()
                .replyText("댓글 텍스트2")
                .replyer("사오정")
                .bno(195L)
                .build();
        log.info(replyService.register(replyDTO));
    }
}
