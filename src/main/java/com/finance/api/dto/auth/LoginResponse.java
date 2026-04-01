package com.finance.api.dto.auth;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    private String token;
    private String type;
    private Integer userId;
    private String email;
    private String role;

    public LoginResponse(String token, Integer userId, String email, String role) {
        this.token = token;
        this.type = "Bearer";
        this.userId = userId;
        this.email = email;
        this.role = role;
    }
}
