package com.dft.mom.domain.repository;

import com.dft.mom.domain.entity.post.Nutrition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NutritionRepository extends JpaRepository<Nutrition, Long> {

    @Query("SELECT n FROM Nutrition n WHERE n.itemId IN :itemIds")
    List<Nutrition> findNutritionListByItemIdIn(List<Long> itemIds);
}
