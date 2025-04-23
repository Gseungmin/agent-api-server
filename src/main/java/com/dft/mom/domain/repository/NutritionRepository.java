package com.dft.mom.domain.repository;

import com.dft.mom.domain.entity.post.Nutrition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface NutritionRepository extends JpaRepository<Nutrition, Long> {

    @Query("SELECT n FROM Nutrition n WHERE n.itemId IN :itemIds")
    List<Nutrition> findNutritionListByItemIdIn(List<Long> itemIds);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Transactional
    @Query("update Nutrition n set n.version = n.version + 1")
    void incrementAllVersions();
}
