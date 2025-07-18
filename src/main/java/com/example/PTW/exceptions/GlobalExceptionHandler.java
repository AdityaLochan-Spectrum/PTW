// GlobalExceptionHandler.java
package com.example.PTW.exceptions;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {
     @Autowired
    private ErrorLogRepository errorLogRepository;


    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected ResponseEntity<Object> handleGlobalException(Exception ex, WebRequest request) {
       // log.error("Exception occurred: ", ex);

        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage());
        String details = ex.getMessage();
        details = StringUtils.abbreviate(details, 5000);
        ErrorLog errorLog = new ErrorLog(errorResponse.getStatus(), errorResponse.getMessage(), errorResponse.getDetails());
//        errorLog.setRequestUri(request.getDescription(false)); // Additional request context
//        errorLog.setTimestamp(LocalDateTime.now());

        try {
            // Code that might throw an exception
            errorLogRepository.save(errorLog);
        } catch (Exception e) {
            // Handle the exception
            System.err.println("Error occurred while saving ErrorLog: " + e.getMessage());
            e.printStackTrace();
        }

        return new ResponseEntity<>(errorResponse, new HttpHeaders(), errorResponse.getStatus());
    }
}
