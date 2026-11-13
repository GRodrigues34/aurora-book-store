package com.github.gr.aurora_bookstore.dto.userDto;

import jakarta.validation.constraints.NotBlank;

public record GoogleLoginDTO(
        @NotBlank(message = "Google credential token is required")
        String credential
) {
}
