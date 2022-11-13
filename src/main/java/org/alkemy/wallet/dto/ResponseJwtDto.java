package org.alkemy.wallet.dto;

import java.io.Serializable;

public class ResponseJwtDto implements Serializable {	

	private static final long serialVersionUID = 6856252483585639593L;
	private final String jwttoken;

	public ResponseJwtDto(String jwttoken) {
		this.jwttoken = jwttoken;
	}

	public String getToken() {
		return this.jwttoken;
	}

}