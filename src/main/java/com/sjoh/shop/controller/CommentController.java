package com.sjoh.shop.controller;

import com.sjoh.shop.model.Comment;
import com.sjoh.shop.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // 댓글 작성
    @PostMapping("/{itemId}")
    public String createComment(@PathVariable Long itemId,
                                 @RequestParam String userId,
                                 @RequestParam String userName,
                                 @RequestParam String content,
                                 @RequestParam(required = false) Long parentId) {

        commentService.createComment(itemId, userId, userName, content, parentId);

        return "redirect:/detail/{itemId}";
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId) {
        Boolean isDeleted = commentService.deleteComment(commentId);
        if(isDeleted) {
            Map<String, String> response = new HashMap<>();
//            response.put("redirectUrl", "/list"); // 리다이렉션 URL 포함
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.badRequest().body("아이템이 삭제되지 않았습니다.");
    }

    // 특정 Item의 댓글 조회
    @GetMapping("/{itemId}")
    public List<Comment> getCommentsByItem(@PathVariable Long itemId) {
        return commentService.getCommentsByItem(itemId);
    }
}
