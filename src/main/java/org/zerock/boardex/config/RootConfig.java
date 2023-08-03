package org.zerock.boardex.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration    //스프링의 설정 클래스임, Bean을 수동으로 등록하기 위해서 설정함.
public class RootConfig {
    @Bean
    public ModelMapper getMapper(){
        ModelMapper modelMapper=new ModelMapper();

        modelMapper.getConfiguration()
                //매칭가능한지 검사함
                .setFieldMatchingEnabled(true)
                //접근 제한자 설정(PRIVATE, PROTECTED, PACKAGE, NONE, MODULE, PUBLIC)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                //MatchingStrategies.STANDARD-지능적 매핑
                //MatchingStrategies.STRICT-정확히 일치 매핑
                //MatchingStrategies.LOOSE-느슨하게 매핑
                .setMatchingStrategy(MatchingStrategies.LOOSE);
        return modelMapper;
    }
}
