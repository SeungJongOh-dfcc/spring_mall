package com.sjoh.shop.repository;

import com.sjoh.shop.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // 특정 Item에 대한 댓글 조회
    List<Comment> findByItemId(Long itemId);

    // 특정 Item에 대한 부모 댓글 조회 (대댓글 제외)
    List<Comment> findByItemIdAndParentIsNull(Long itemId);

    Optional<Comment> findByUserId(String userId);
}
