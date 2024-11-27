package com.jpeccia.levelinglife.dto;

import jakarta.validation.constraints.Pattern;

public record UserRegisterDTO(
    String username,
    String name,
    String email,

    @Pattern(
        regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
        message = "A senha deve ter pelo menos 8 caracteres, incluindo uma letra maiúscula, uma letra minúscula, um número e um caractere especial."
    )
    String password
) {
}