package org.alkemy.wallet.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Data
public class UserRegisterRequestDto implements Serializable {

    @NotEmpty
    @Schema(description = "First name", example = "Werner")
    private String firstName;

    @NotEmpty
    @Schema(description = "Last name", example = "Heisenberg")
    private String lastName;

    @NotEmpty
    @Schema(description = "Email address", example = "wernerh@alkemy.com")
    private String email;

    @NotEmpty
    @Schema(description = "Password", example = "12345678")
    private String password;
}
