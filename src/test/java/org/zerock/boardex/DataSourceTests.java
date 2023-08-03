package org.zerock.boardex;

import lombok.Cleanup;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

//@Autowired : 필요한 의존 객체의 '타입'에 해당하는 Bean을 찾아 주입해 줌(생성자, setter, 필드 등)
@SpringBootTest
@Log4j2
public class DataSourceTests {
    @Autowired
    private DataSource dataSource;
    @Test
    public void testConnection() throws SQLException {
        @Cleanup Connection con=dataSource.getConnection();
        log.info(con);
        //Assertion : 인수 검증하고 조건에 맞지 않으면 예외처리함.
        Assertions.assertNotNull(con);
    }
}
