package com.library.management.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResetPasswordForm {
    private Integer id;
    private String name;
    private String email;
    private String newPassword;
    private String confirmPassword;
}