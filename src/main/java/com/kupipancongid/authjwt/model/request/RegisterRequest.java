package com.kupipancongid.authjwt.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.annotation.Order;

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
    @Size(max = 32)
    String password;
    @NotBlank
    @Size(max = 32)
    @JsonProperty("password_confirmation")
    String passwordConfirmation;
}
