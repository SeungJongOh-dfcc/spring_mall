package com.sjoh.shop.service;

import com.sjoh.shop.repository.UserRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    MyUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        // DB에서 userId를 가진 걸 조회
        // return new User(userId);
        Optional<com.sjoh.shop.model.User> result = userRepository.findByUserId(userId);
        if(result.isEmpty()) {
            throw new UsernameNotFoundException("일치하는 회원이 없습니다.");
        }
        com.sjoh.shop.model.User user = result.get();
        List<GrantedAuthority> authorityList = user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName()))
                                .collect(Collectors.toList());

        CustomUser customUser = new CustomUser(user.getUserId(), user.getPassword(), authorityList, user.getId(), user.getUsername());

        return customUser;
    }

    @Getter
    @Setter
    public static class CustomUser extends User {
        private Long id;
        private String displayName;
        public CustomUser(String username,
                          String password,
                          List<GrantedAuthority> authorities,
                          Long id,
                          String displayName) {
            super(username, password, authorities);
            this.displayName = displayName;
            this.id = id;
        }
    }
}