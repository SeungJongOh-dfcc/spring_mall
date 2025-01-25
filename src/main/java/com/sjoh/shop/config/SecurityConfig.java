package com.sjoh.shop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http.csrf((csrf) -> csrf.disable());  // csrf 끄기
        http.csrf((csrf) -> csrf.csrfTokenRepository(csrfTokenRepository())
                .ignoringRequestMatchers("/login", "/h2-console/**"));

        http.authorizeHttpRequests(
                (authorize) ->
                authorize.requestMatchers("/**").permitAll()
        ).headers((headers) -> headers
                .frameOptions((frameOptions -> frameOptions.sameOrigin()))
        );

        // formLogin 설정
        http.formLogin(form -> form
                .loginPage("/login")    // 사용자 정의 로그인 페이지
                .loginProcessingUrl("/login")   // 로그인 처리 URL
                .usernameParameter("userId")    // 사용자 아이디 input name 속성
                .passwordParameter("password")  // 사용자 비밀번호 input name 속성
                .defaultSuccessUrl("/list")
                .failureUrl("/login?error=true")    // 로그인 실패 시 이동할 URL
        );

        // 로그아웃
        http.logout(logout -> logout.logoutUrl("/logout"));

        return http.build();
    }

//    @Bean
//    SimpleGrantedAuthority simpleGrantedAuthority() {
//        return new SimpleGrantedAuthority();
//    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // CSRF
    @Bean
    public CsrfTokenRepository csrfTokenRepository() {
        HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
        repository.setHeaderName("X-XSRF-TOKEN");
        return repository;
    }
}
