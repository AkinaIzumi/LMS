package com.library.management.dto;

import com.library.management.model.AccountStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AdminForm {
    private String name;
    private String username;
    private String email;
    private String password;
    private String confirmPassword;
    private AccountStatus status = AccountStatus.ACTIVE;
}
