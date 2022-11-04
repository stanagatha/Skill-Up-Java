package org.alkemy.wallet.exception;

public class NotFoundException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = -6968342763567608288L;

    public NotFoundException(String detail) {
        super(detail);
    }

}
