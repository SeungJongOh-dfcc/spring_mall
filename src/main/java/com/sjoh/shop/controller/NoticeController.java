package com.sjoh.shop.controller;

import com.sjoh.shop.model.Notice;
import com.sjoh.shop.service.NoticeService;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/notice")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @GetMapping("/list")
    public String getList(Model model) {
        List<Notice> noticeAll = noticeService.getNoticeAll();
        model.addAttribute("noticeAll", noticeAll);

        return "page/notice/list";
    }

    @GetMapping("/detail/{id}")
    public String getList(@PathVariable Long id, Model model) throws Exception {
        Notice noticeItem = noticeService.getNotice(id);

        model.addAttribute("notice", noticeItem);

        return "page/notice/detail";
    }
}

@ToString
class 클래스 {
    private String name;
    private Integer age = 0;

    public String getName() {
        return name;
    }

    public Integer getAge() {
        return age;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean setAge(Integer age) {
        if(age == null || age < 0 || age >= 100) return false;
        this.age = age;
        return true;
    }

    public void setAgeUp() {
        this.age++;
    }
}