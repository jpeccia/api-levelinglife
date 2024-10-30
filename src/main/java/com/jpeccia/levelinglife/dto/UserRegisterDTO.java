package com.jpeccia.levelinglife.dto;

import com.jpeccia.levelinglife.entity.UserRole;

public record UserRegisterDTO (String username, String name, String email, String password, UserRole role) {

}
