package com.group8.projectmanager.dtos;

import jakarta.validation.constraints.NotBlank;

public record PasswordChangeDto(@NotBlank
                                String OriginalPassword,
                                @NotBlank String NewPassword,
                                @NotBlank String ReconfirmNewPassword) {
}
