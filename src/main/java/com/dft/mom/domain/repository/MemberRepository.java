package com.dft.mom.domain.repository;

import com.dft.mom.domain.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query("select m from Member m " +
            "where m.id = :id")
    Optional<Member> findById(Long id);

    @Query("select m from Member m " +
            "left join fetch m.roles " +
            "where m.socialId = :socialId")
    Optional<Member> findBySocialId(String socialId);

    @Query("select m from Member m " +
            "left join fetch m.auth " +
            "where m.id = :id")
    Optional<Member> findByIdWithAuth(Long id);

    @Query("select m from Member m " +
            "join fetch m.family " +
            "where m.id = :id")
    Optional<Member> findByIdWithFamily(Long id);

    @Query("select m from Member m " +
            "join fetch m.family f " +
            "where f.id = :id")
    List<Member> findMemberListByFamilyId(Long id);
}
