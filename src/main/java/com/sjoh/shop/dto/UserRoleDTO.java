package com.sjoh.shop.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRoleDTO {
    private String userId;
    private String roleName;

    public UserRoleDTO(String userId, String roleName) {
        this.userId = userId;
        this.roleName = roleName;
    }

}
