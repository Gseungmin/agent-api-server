package com.dft.mom.domain.repository;

import com.dft.mom.domain.entity.post.Inspection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InspectionRepository extends JpaRepository<Inspection, Long> {

    @Query("SELECT i FROM Inspection i WHERE i.itemId IN :itemIds")
    List<Inspection> findInspectionListByItemIdIn(List<Long> itemIds);
}
