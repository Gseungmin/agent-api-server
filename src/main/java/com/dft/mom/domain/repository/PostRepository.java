package com.dft.mom.domain.repository;

import com.dft.mom.domain.entity.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p WHERE p.itemId IN :itemIds")
    List<Post> findPostListByItemIdIn(List<Long> itemIds);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Transactional
    @Query("update Post p set p.version = p.version + 1")
    void incrementAllVersions();
}
