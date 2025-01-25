package com.sjoh.shop.component;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.Collectors;

@Component
public class AuthorityMapper {

    public String mapToKorean(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .map(auth -> {
                    switch (auth.getAuthority()) {
                        case "ROLE_ADMIN":
                            return "관리자";
                        case "ROLE_USER":
                            return "일반회원";
                        default:
                            return "알 수 없음";
                    }
                })
                .collect(Collectors.joining(", "));
    }
}
