package org.zerock.boardex.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.zerock.boardex.security.CustomUserDetailsService;
import org.zerock.boardex.security.handler.Custom403Handler;
import org.zerock.boardex.security.handler.CustomSocialLoginSuccessHandler;

import javax.sql.DataSource;
import java.io.IOException;

@Log4j2
@Configuration  //환경설정파일을 만듬
@RequiredArgsConstructor    //final이 붙거나 @NotNull이 붙은 필드의 생성자를 자동 생성해주는 lombok annotation

//권한 설정
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class CustomSecurityConfig {
    private final DataSource dataSource;
    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler(){
        return new CustomSocialLoginSuccessHandler(passwordEncoder());
    }
    @Bean   //수동으로 Bean 등록
    //SecurityFilterChain : 스프링 부트에서 지원하는 기본 로그인 화면으로 접속하지 못하게 함.
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("****** configure *******");
        //사용자 인증 처리
        http.formLogin().loginPage("/member/login");

        http.rememberMe()
                .key("12345678")
                .tokenRepository(persistentTokenRepository())
                .userDetailsService(customUserDetailsService)
                .tokenValiditySeconds(60*60*24*30);

        //exceptionHandling() : Error발생시 후처리 설정
        http.exceptionHandling().accessDeniedHandler(accessDeniedHandler());

        //카카오 로그인하기
        http.oauth2Login().loginPage("/member/login")
                .successHandler(authenticationSuccessHandler());

        http.logout()
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .logoutUrl("/member/logout") // 로그아웃 처리 URL, 원칙적으로 post 방식만 지원
                .logoutSuccessUrl("/")
                //.deleteCookies("JSESSIONID", "remember-me")
                .addLogoutHandler(new LogoutHandler() {
                    @Override
                    public void logout(HttpServletRequest request,
                                       HttpServletResponse response,
                                       Authentication authentication) {
                        HttpSession session = request.getSession();
                        session.invalidate();
                    }
                })//로그아웃 핸들러
                .logoutSuccessHandler(new LogoutSuccessHandler() {
                    @Override
                    public void onLogoutSuccess(HttpServletRequest request,
                                                HttpServletResponse response,
                                                Authentication authentication) throws IOException, ServletException {
                        response.sendRedirect("/member/login?logout");
                    }
                })//로그아웃 성공 후 핸들러
                .deleteCookies("JSESSIONID", "remember-me");

        return http.build();
    }
    @Bean
    //WebSecurityCostomizer : 적용하지 않을 리소스를 설정
    public WebSecurityCustomizer webSecurityCustomizer(){
        log.info("****** web configure *******");
        return (web) -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }
    //비밀번호 암호화
    @Bean
    public PasswordEncoder passwordEncoder (){
        return new BCryptPasswordEncoder();
    }
    
    //자동로그인을 위한 환경설정
    @Bean
    public PersistentTokenRepository persistentTokenRepository(){
        //쿠키 정보를 테이블로 저장하도록 설정
        JdbcTokenRepositoryImpl repo=new JdbcTokenRepositoryImpl();
        repo.setDataSource(dataSource);
        return repo;
    }
    //정식 사용자가 아닌데 게시글 수정하려고 접근했을 때 서버에서 접근 거부하면 예외처리함
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new Custom403Handler();
    }

    //인증된 사용자의 mid(email)값 가져오기
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}
