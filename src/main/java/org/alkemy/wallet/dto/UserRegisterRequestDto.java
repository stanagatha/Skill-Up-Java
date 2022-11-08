package org.alkemy.wallet.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserRegisterRequestDto implements Serializable {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
}
