package org.alkemy.wallet.dto;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserLoginDto implements Serializable {

    private static final long serialVersionUID = 2785900954053998543L;

    @Schema(description = "User's email address", example = "nataliag@alkemy.com")
    private String email;
    @Schema(description = "User's password", example = "12345678")
    private String password;

}