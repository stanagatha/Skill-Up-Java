package org.alkemy.wallet.exception;

public class UnAuthorizedException extends RuntimeException{
    public UnAuthorizedException(String message){
        super(message);
    }
}
