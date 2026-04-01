package com.finance.api.dto.user;

import com.finance.domain.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserRequest {

    @Size(min = 2, max = 255, message = "Name must be between 2 and 255 characters")
    private String name;

    @Email(message = "Email should be valid")
    private String email;

    private UserRole role;
}
