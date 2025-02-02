package com.sjoh.shop.controller;

import com.sjoh.shop.component.AuthorityMapper;
import com.sjoh.shop.dto.UserDto;
import com.sjoh.shop.dto.UserRoleDTO;
import com.sjoh.shop.service.MyUserDetailsService;
import com.sjoh.shop.service.UserService;
import com.sjoh.shop.util.CustomUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class UserController {

    private final UserService userService;
    private final AuthorityMapper authorityMapper;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserController(
            UserService userService,
            AuthorityMapper authorityMapper,
            AuthenticationManager authenticationManager
            ) {
        this.userService = userService;
        this.authorityMapper = authorityMapper;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "page/member/login";
    }

    @GetMapping("/check-userId")
    public ResponseEntity<Map<String, Boolean>> checkUserId(@RequestParam String userId) {
        boolean isUserIdAvailable = userService.isUserIdAvailable(userId);
        Map<String, Boolean> response = new HashMap<>();
        response.put("available", isUserIdAvailable);

        return ResponseEntity.ok(response);
    }

//    @PostMapping("/login")
//    public String login(@ModelAttribute User user) {
//        System.out.println("user = " + user);
//        String userId = user.getUserId();
//        String password = user.getPassword();
//        boolean isLogin = userService.login(userId, password);
//        if(isLogin) {
//            return "redirect:/list";
//        } else {
//            return "error";
//        }
//    }

    @GetMapping("/signup")
    public String signupPage(Authentication auth) {
        // null일 때 NullPointException 생김. 조건 체크해야함.
        if(auth != null && auth.isAuthenticated()) {
            return "redirect:/list";
        } else {
            return "page/member/signup";
        }
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute @Valid UserDto userDto,
                         BindingResult result,
                         HttpServletRequest request) throws Exception {
        if (result.hasErrors()) {
            return "page/member/signup";
        }

        userService.signup(userDto); // 회원가입 처리

        // 회원가입 후 자동 로그인 처리
        authenticateUserAndSetSession(userDto, request);

        return "redirect:/list";
    }


    @GetMapping("/my-page")
    public String myPage(Authentication auth, Model model) {

        if(auth != null && auth.isAuthenticated()) {
            // 권한 얻기
            Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
            String authorityInKorean = authorityMapper.mapToKorean(authorities);

            model.addAttribute("authority", authorityInKorean);
            // *권한 얻기

            // 닉네임 얻기
//            String userName = userService.getUsernameByUserId(auth.getName());    // DB 호출
                MyUserDetailsService.CustomUser userInfo = (MyUserDetailsService.CustomUser) auth.getPrincipal();
                System.out.println("userInfo = " + userInfo);
                String displayName = userInfo.getDisplayName();

                model.addAttribute("userName", displayName);


            // *닉네임 얻기

            return "page/myPage/myPage";
        } else {
            return "page/member/login";
        }
    }

    @GetMapping("/authority")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String authorityPage(Model model) throws Exception {
        List<UserRoleDTO> userAuthorityAll = userService.getUserAuthorityAll();

        model.addAttribute("authData", userAuthorityAll);

        return "page/authority/authority";
    }

    private void authenticateUserAndSetSession(UserDto userDto, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(userDto.getUserId(), userDto.getPassword());

        // 인증 처리
        Authentication authenticate = authenticationManager.authenticate(authToken);

        // SecurityContext에 인증 정보 설정
        SecurityContextHolder.getContext().setAuthentication(authenticate);

        // 세션에 SecurityContext 저장
        HttpSession session = request.getSession(true);
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext()
        );
    }
}
