package com.example.covault.configs;

import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class GlobalExceptionHandler {

//    @ExceptionHandler(InvalidJWTException.class)
//    public ResponseEntity<ApiResponse<Void>> handleInvalidJwt(InvalidJWTException ex) {
//        ApiResponse<Void> response = new ApiResponse<>(
//                ex.getMessage(),
//                HttpStatus.UNAUTHORIZED,
//                null
//        );
//        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
//    }
}
