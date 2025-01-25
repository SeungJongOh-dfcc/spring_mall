package com.sjoh.shop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class BasicController {
    @GetMapping("/hello")
    @ResponseBody   // 문자 그대로 보내주세요.
    String hello(@RequestParam("username") String username) {
        return "<h2>반갑소, " + username + "</h2>";
    }
    
//    @GetMapping("/about")
////    @ResponseBody // 안 없애면 index.html 문자열이 찍힘
//    String about() {
//        return "index.html";
//    }

//    @GetMapping("/my-page")
//    @ResponseBody
//    String myPage() {
//        return "마이페이지에오";
//    }
}
