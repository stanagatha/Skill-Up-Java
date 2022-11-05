package org.alkemy.wallet.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class UserLoginDto implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2785900954053998543L;
	private String email;
	private String password;
}