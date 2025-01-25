package com.sjoh.shop.repository;

import com.sjoh.shop.dto.UserRoleDTO;
import com.sjoh.shop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserIdAndPassword(String userId, String password);

    Optional<User> findByUserId(String userId);

    Optional<User> findByUsername(String username);

    @Query("SELECT u.username FROM User u WHERE u.userId = :userId")
    String findUsernameByUserId(@Param("userId") String userId);

    @Query(value = """
        SELECT a.user_id AS userId, c.name AS roleName
        FROM SPRING_MALL_USER a
        LEFT JOIN USER_ROLES b ON a.id = b.user_id
        LEFT JOIN ROLE c ON b.role_id = c.id
        """, nativeQuery = true)
    Optional<List<UserRoleDTO>> findUserRolesAll();
}
