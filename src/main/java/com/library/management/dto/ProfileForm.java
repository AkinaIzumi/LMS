package com.library.management.dto;

import com.library.management.model.AccountStatus;
import com.library.management.model.Role;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProfileForm {
    private Integer id;
    private String name;
    private String username;
    private String email;
    private Role role;
    private AccountStatus status;
}