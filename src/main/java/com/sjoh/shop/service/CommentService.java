package com.sjoh.shop.service;

import com.sjoh.shop.model.Comment;
import com.sjoh.shop.model.Item;
import com.sjoh.shop.repository.CommentRepository;
import com.sjoh.shop.repository.ItemRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository, ItemRepository itemRepository) {
        this.commentRepository = commentRepository;
        this.itemRepository = itemRepository;
    }

    // 댓글 작성
    @Transactional
    public Comment createComment(Long itemId, String userId, String userName, String content, Long parentId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new IllegalArgumentException("Item not found"));

        Comment comment = new Comment();
        comment.setItem(item);
        comment.setUserId(userId);
        comment.setUserName(userName);
        comment.setContent(content);

        if(parentId != null) {
            Comment parentComment = commentRepository.findById(parentId).orElseThrow(
                    () -> new IllegalArgumentException("Parent comment with userId " + parentId + " not found")
            );
            comment.setParent(parentComment);
        }

        return commentRepository.save(comment);
    }

    // 특정 Item에 대한 댓글 조회
    public List<Comment> getCommentsByItem(Long itemId) {
        return commentRepository.findByItemIdAndParentIsNull(itemId);
    }

    public Boolean deleteComment(Long commentId) {
        Optional<Comment> comment = commentRepository.findById(commentId);
        if(comment.isPresent()) {
            commentRepository.deleteById(commentId);
            return true;
        } else {
            return false;
        }
    }
}
