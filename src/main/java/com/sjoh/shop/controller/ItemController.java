package com.sjoh.shop.controller;

import com.sjoh.shop.model.Comment;
import com.sjoh.shop.model.Item;
import com.sjoh.shop.service.CommentService;
import com.sjoh.shop.service.ItemService;
import com.sjoh.shop.service.MyUserDetailsService;
import com.sjoh.shop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
public class ItemController {

    private final ItemService itemService;
//    private final UserService userService;
    private final CommentService commentService;

    @Autowired
    public ItemController(ItemService itemService, CommentService commentService) {
        this.itemService = itemService;
        this.commentService = commentService;
    }

//    @GetMapping("/list")
//    public String list(Model model) {
//        ArrayList<Item> itemAll = itemService.getItemAll();
//        model.addAttribute("itemAll", itemAll);
//        return "page/item/list";
//    }

    @GetMapping("/list")
    public String getListPage(@RequestParam(defaultValue = "1") Integer page, Model model) {
        Page<Item> pageItemList = itemService.getPageItemList(PageRequest.of(page - 1, 5));
        model.addAttribute("itemAll", pageItemList);
        return "page/item/list";
    }

    @GetMapping("/write")
    public String write() {
        return "page/item/write";
    }

    @PostMapping("/add")
    public String savedItem(
//            @RequestParam HashMap<String, Object> formData
            @ModelAttribute Item formData,
            RedirectAttributes redirectAttributes,
            Authentication auth
    ) throws Exception {

        try {
            boolean isSaved = itemService.savedItem(formData, auth.getName());
            if(isSaved) {
                return "redirect:/list";
            } else {
             redirectAttributes.addFlashAttribute("errorMessage", "아이템 저장에 실패했습니다.");
             return "redirect:/add";
            }
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/add";
        }
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes, Authentication auth) {
        try {
            Item item = itemService.findItemById(id);
            model.addAttribute("item", item);

            // 댓글
            List<Comment> comments = commentService.getCommentsByItem(id);
            model.addAttribute("comments", comments);

            if(auth != null) {
                String authId = auth.getName();
                MyUserDetailsService.CustomUser userInfo = (MyUserDetailsService.CustomUser) auth.getPrincipal();
                String displayName = userInfo.getDisplayName();
//                Object authId = userService.getIdByUserId(authName);
                model.addAttribute("authId", authId);
                model.addAttribute("authName", displayName);
            } else {
                model.addAttribute("authId", "");
                model.addAttribute("authName", "");
            }

            return "page/item/detail";
        } catch (NoSuchElementException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/list";
        }
    }

    @PatchMapping("edit/{id}")  // PATCH 요청은 redirect이 안됨.
    public ResponseEntity<Map<String, String>> editItem(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        itemService.partiallyUpdateItem(id, updates);
        Map<String, String> response = new HashMap<>();
        response.put("redirectUrl", "/list"); // 리다이렉션 URL 포함
        return ResponseEntity.ok(response);
    }

    @GetMapping("/edit/{id}")
    public String write(@PathVariable Long id, Model model) {
        Item item = itemService.findItemById(id);
        model.addAttribute("item", item);

        return "page/item/edit";
    }

    @DeleteMapping("/detail/{id}")
    public ResponseEntity<?> deleteItem(@PathVariable Long id) {
        boolean isDeleted = itemService.deleteItem(id);
        if(isDeleted) {
            Map<String, String> response = new HashMap<>();
            response.put("redirectUrl", "/list"); // 리다이렉션 URL 포함
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.badRequest().body("아이템이 삭제되지 않았습니다.");
    }

    @GetMapping("/search")
    public String postSearch(@RequestParam String searchText) {
        return "";
    }
}
