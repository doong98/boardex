package org.zerock.boardex.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.boardex.domain.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, String> {
    //@EntityGraph : 엔티티들은 서로 연관되어 있는 관계가 보통이며, 그 관계는 그래프로 표현이 가능하다. EntityGraph는 JPA가 어떤 엔티티를 불러올 때 그 엔티티와 관계된 엔티티를 불러올 것인지에 대한 정보 제공
    //데이터베이스의 Member테이블에서 멤버아이디가 같고 소셜로그인하지 않은 데이터를 조회하라는 쿼리문 실행
    @EntityGraph(attributePaths="roleSet")
    @Query("select m from Member m where m.mid = :mid and m.social = false")
    Optional<Member> getWithRoles(@Param("mid") String mid);
    @EntityGraph(attributePaths = "roleSet")
    Optional<Member> findByEmail(String email);

    //비밀번호 수정
    @Modifying
    @Transactional
    @Query("update Member m set m.mpw = :mpw where m.mid = :mid")
    void updatePassword(@Param("mpw") String password, @Param("mid") String mid);
}
