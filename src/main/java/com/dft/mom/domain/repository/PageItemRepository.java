package com.dft.mom.domain.repository;

import com.dft.mom.domain.entity.post.BabyPage;
import com.dft.mom.domain.entity.post.BabyPageItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PageItemRepository extends JpaRepository<BabyPageItem, Long> {

    @Query("select bpi from BabyPageItem bpi " +
            "join fetch bpi.post p " +
            "join fetch bpi.babyPage bp " +
            "where bpi.post.id in :idList")
    List<BabyPageItem> findBabyPageItemByIdList(List<Long> idList);

    @Query("select bpi from BabyPageItem bpi " +
            "join fetch bpi.nutrition n " +
            "join fetch bpi.babyPage bp " +
            "where bpi.nutrition.id in :idList")
    List<BabyPageItem> findBabyPageItemByNutritionIdList(List<Long> idList);

    @Query("select bpi from BabyPageItem bpi " +
            "join fetch bpi.inspection i " +
            "join fetch bpi.babyPage bp " +
            "where bpi.inspection.id in :idList")
    List<BabyPageItem> findBabyPageItemByInspectionIdList(List<Long> idList);

    @Query("select bp from BabyPageItem bp " +
            "join fetch bp.post p " +
            "where bp.babyPage = :babyPage")
    List<BabyPageItem> findBabyPageItemWithPost(BabyPage babyPage);

    @Query("select bp from BabyPageItem bp " +
            "join fetch bp.nutrition n " +
            "where bp.babyPage = :babyPage")
    List<BabyPageItem> findBabyPageItemWithNutrition(BabyPage babyPage);

    @Query("select bp from BabyPageItem bp " +
            "join fetch bp.inspection i " +
            "where bp.babyPage = :babyPage")
    List<BabyPageItem> findBabyPageItemWithInspection(BabyPage babyPage);
}
