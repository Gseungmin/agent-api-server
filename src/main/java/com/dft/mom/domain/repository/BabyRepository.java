package com.dft.mom.domain.repository;

import com.dft.mom.domain.entity.family.Baby;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BabyRepository extends JpaRepository<Baby, Long> {

    @Query("select b from Baby b " +
            "join fetch b.family " +
            "where b.id = :babyId")
    Optional<Baby> findBabyById(Long babyId);

    @Query("select b from Baby b " +
            "left join fetch b.family f " +
            "where f.id = :familyId")
    List<Baby> findBabyListByFamilyId(Long familyId);
}
