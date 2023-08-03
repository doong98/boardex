package org.zerock.boardex.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.zerock.boardex.domain.Member;
import org.zerock.boardex.dto.MemberSecurityDTO;
import org.zerock.boardex.repository.MemberRepository;

import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;

    //UsernameNotFoundException : username을 가진 사용자가 없다면 예외처리함.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //사용자 아이디를 콘솔에 표시
        log.info("loadUserByUsername: "+username);
        //데이터베이스에서 사용자 아이디의 권한과 정보 찾아서 result에 저장
        Optional<Member> result=memberRepository.getWithRoles(username);
        //해당 아이디를 가진 사용자가 없다면
        if(result.isEmpty()){
            throw new UsernameNotFoundException("사용자가 없습니다.");
        }
        //사용자 아이디가 있다면 그 해당정보를 얻어서 member에 저장
        Member member=result.get();
        //MemberSecurityDTO객체의 아이디, 비밀번호, 이메일, del값, social(false), 권한정보 등을 매핑
        //SimpleGrantedAuthority : GrantedAuthority를 상속받은 클래스임.(ROLE_USER, ROLE_ADMIN 둘 중 하나)
        MemberSecurityDTO memberSecurityDTO=new MemberSecurityDTO(
                member.getMid(),
                member.getMpw(),
                member.getEmail(),
                member.isDel(),
                false,
                member.getRoleSet()
                        .stream().map(memberRole -> new SimpleGrantedAuthority("ROLE_"+memberRole.name()))
                        .collect(Collectors.toList())
        );
        log.info("memberSecurityDTO");
        log.info(memberSecurityDTO);
        return memberSecurityDTO;
    }
}
