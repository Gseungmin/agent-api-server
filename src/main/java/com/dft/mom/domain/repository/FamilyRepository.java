package com.dft.mom.domain.repository;

import com.dft.mom.domain.entity.family.Family;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FamilyRepository extends JpaRepository<Family, Long> {

    @Query("select f from Family f " +
            "where f.code = :code")
    Optional<Family> findFamilyByCode(String code);
}
