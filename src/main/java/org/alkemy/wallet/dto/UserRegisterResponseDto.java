package org.alkemy.wallet.dto;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class UserRegisterResponseDto implements Serializable {

	private static final long serialVersionUID = 4701324765251360993L;

	private Long id;
	private String firstName;
    private String lastName;
    private String email;
    private String roleName;
    @JsonFormat(pattern = "yyyy/MM/dd")
    private Date creationDate;
    @JsonFormat(pattern = "yyyy/MM/dd")
    private Date updateDate;
    private String token;
}
