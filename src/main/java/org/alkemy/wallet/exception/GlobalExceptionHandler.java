package org.alkemy.wallet.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({BadRequestException.class, IllegalArgumentException.class})
    @ResponseBody
    public ResponseEntity<String> badRequestExceptionHandler(RuntimeException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({UnAuthorizedException.class})
    @ResponseBody
    public ResponseEntity<String> unAuthorizedExceptionHandler(RuntimeException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({ForbiddenException.class})
    @ResponseBody
    public ResponseEntity<String> forbiddenExceptionHandler(RuntimeException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({NotFoundException.class})
    @ResponseBody
    public ResponseEntity<String> notFoundExceptionHandler(RuntimeException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

}
