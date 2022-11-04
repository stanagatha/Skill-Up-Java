package org.alkemy.wallet.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class UserDto implements Serializable {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String roleName;
    @JsonFormat(pattern = "yyyy/MM/dd")
    private Date creationDate;
    @JsonFormat(pattern = "yyyy/MM/dd")
    private Date updateDate;
    private Boolean softDelete;

}
