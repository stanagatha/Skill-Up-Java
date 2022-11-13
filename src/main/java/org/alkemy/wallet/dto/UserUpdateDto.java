package org.alkemy.wallet.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserUpdateDto implements Serializable {

    public String firstName;
    public String lastName;
}
