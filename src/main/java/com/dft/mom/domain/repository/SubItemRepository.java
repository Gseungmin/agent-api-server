package com.dft.mom.domain.repository;

import com.dft.mom.domain.entity.post.SubItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubItemRepository extends JpaRepository<SubItem, Long> {

    @Query("select si from SubItem si " +
            "join fetch si.post p " +
            "where si.post.itemId = :id")
    List<SubItem> findSubItemListByPostId(Long id);

    @Query("select si from SubItem si " +
            "join fetch si.nutrition p " +
            "where si.nutrition.itemId = :id")
    List<SubItem> findSubItemListByNutritionId(Long id);

    @Query("select si from SubItem si " +
            "join fetch si.inspection p " +
            "where si.inspection.itemId = :id")
    List<SubItem> findSubItemListByInspectionId(Long id);
}
