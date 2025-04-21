package com.dft.mom.domain.repository;

import com.dft.mom.domain.entity.post.BabyPage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PageRepository extends JpaRepository<BabyPage, Long> {

    @Query("select bp from BabyPage bp " +
            "where bp.type = :type and bp.period = :period")
    Optional<BabyPage> findBabyByTypeAndPeriod(Integer type, Integer period);
}
