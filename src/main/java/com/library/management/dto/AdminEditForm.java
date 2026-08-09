package com.library.management.dto;

import com.library.management.model.AccountStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AdminEditForm  {
    private Integer id;
    private String name;
    private String username;
    private String email;
    private AccountStatus status;
}
