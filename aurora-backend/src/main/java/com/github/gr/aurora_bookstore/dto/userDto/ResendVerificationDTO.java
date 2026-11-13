package com.github.gr.aurora_bookstore.dto.userDto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ResendVerificationDTO(
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email
) {
}
