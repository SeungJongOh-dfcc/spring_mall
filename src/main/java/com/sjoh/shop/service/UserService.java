package com.sjoh.shop.service;

import com.sjoh.shop.dto.UserDto;
import com.sjoh.shop.dto.UserRoleDTO;
import com.sjoh.shop.model.Role;
import com.sjoh.shop.model.User;
import com.sjoh.shop.repository.RoleRepository;
import com.sjoh.shop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Autowired
    UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    public void signup(UserDto userDto) throws Exception {
        Optional<User> userCheck = userRepository.findByUsername(userDto.getUsername());
        // 조회되면 이미 존재하는 아이디.
        if(userCheck.isPresent()) {
            throw new Exception("존재하는 아이디입니다.");
        }

        // 아이디 비밀번호 자릿 수가 너무 짧은 경우
        if(userDto.getUserId().length() < 4 || userDto.getPassword().length() < 4) {
            throw new Exception("아이디와 비밀번호가 너무 짧습니다.");
        }

        String hashedPassword = passwordEncoder.encode(userDto.getPassword());

        // 신규 유저 정보
        User newUser = new User();
        newUser.setUserId(userDto.getUserId());
        newUser.setUsername(userDto.getUsername());
        newUser.setPassword(hashedPassword);
        //////////////////

        Role userRole = roleRepository.findByName("ROLE_USER")
                        .orElseThrow(() -> new RuntimeException("기본 권한이 존재하지 않습니다."));

        newUser.getRoles().add(userRole);

        userRepository.save(newUser);

    }

    public boolean login(String userId, String password) {
        try {
            Optional<User> loginCheck = userRepository.findByUserId(userId);
            if(loginCheck.isPresent()) {
                return new BCryptPasswordEncoder().matches(password, loginCheck.get().getPassword());
            } else {
                return false;
            }
        } catch (Exception e) {
            System.out.println("error occurred: " + e);
            return false;
        }
    }

    public boolean isUserIdAvailable(String userId) {
        return userRepository.findByUserId(userId).isEmpty();
    }

    public String getUsernameByUserId(String userId) {
        return userRepository.findUsernameByUserId(userId);
    }

    public List<UserRoleDTO> getUserAuthorityAll() throws Exception {
        Optional<List<UserRoleDTO>> userRolesAll = userRepository.findUserRolesAll();
        if (userRolesAll.isEmpty()) {
            throw new Exception("권한을 가진 유저가 없습니다.");
        }

        return userRolesAll.get();
    }
}
