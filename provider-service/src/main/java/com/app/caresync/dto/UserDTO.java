package com.app.caresync.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long userId;
    private String fullName;
    private String email;
    private String phone;
}
