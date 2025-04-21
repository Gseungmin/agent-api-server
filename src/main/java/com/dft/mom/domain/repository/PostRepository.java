package com.dft.mom.domain.repository;

import com.dft.mom.domain.entity.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("select p from Post p " +
            "where p.itemId = :itemId")
    Optional<Post> findPostByItemId(Long itemId);

    @Query("SELECT p FROM Post p WHERE p.itemId IN :itemIds")
    List<Post> findPostListByItemIdIn(List<Long> itemIds);
}
