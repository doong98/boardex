package org.zerock.boardex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
//AuditingEntityListener를 활성화시키기 위한 어노테이션
@EnableJpaAuditing
public class BoardExApplication {

    public static void main(String[] args) {
        SpringApplication.run(BoardExApplication.class, args);
    }

}
