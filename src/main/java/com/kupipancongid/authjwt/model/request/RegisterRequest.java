package com.kupipancongid.authjwt.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequest {
    @NotBlank
    String name;
    @NotBlank
    @Email
    String email;
    @NotBlank
    @Max(32)
    String password;
    @NotBlank
    @Max(32)
    String passwordConfirmation;
}
