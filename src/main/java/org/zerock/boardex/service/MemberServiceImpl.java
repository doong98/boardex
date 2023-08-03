package org.zerock.boardex.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.zerock.boardex.domain.Member;
import org.zerock.boardex.domain.MemberRole;
import org.zerock.boardex.dto.MemberJoinDTO;
import org.zerock.boardex.repository.MemberRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final ModelMapper modelMapper;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    @Override
    public void join(MemberJoinDTO memberJoinDTO) throws MidExistException {
        String mid=memberJoinDTO.getMid();
        boolean exist=memberRepository.existsById(mid);
        if(exist){
            throw new MidExistException();
        }
        Member member=modelMapper.map(memberJoinDTO, Member.class);
        member.changePassword(passwordEncoder.encode(memberJoinDTO.getMpw()));
        member.addRole(MemberRole.USER);
        log.info("*******************");
        log.info(member);
        log.info(member.getRoleSet());
        memberRepository.save(member);
    }
    //로그인한 사용자 이름(이메일) 가져오기
    @Override
    public UserDetails loadUserByUsername(String mid) throws UsernameNotFoundException {
        Optional<Member> _memUser=this.memberRepository.findById(mid);
        if(_memUser.isEmpty()){
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }
        Member member=_memUser.get();
        List<GrantedAuthority> authorities=new ArrayList<>();
        if("admin".equals(mid)){
            authorities.add(new SimpleGrantedAuthority(MemberRole.ADMIN.getValue()));
        }else{
            authorities.add(new SimpleGrantedAuthority(MemberRole.USER.getValue()));
        }
        return new User(member.getMid(), member.getMpw(), authorities);
    }
}
