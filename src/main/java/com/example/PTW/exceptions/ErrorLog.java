package com.example.PTW.exceptions;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table
@Entity
public class ErrorLog {
     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int statusCode;
    private String message;
    @Column(name = "details", columnDefinition = "LONGTEXT")
    private String details;
    private LocalDateTime timestamp;
     // Constructor to be used in the ExceptionHandler
     public ErrorLog(int statusCode, String message, String details) {
        this.statusCode = statusCode;
        this.message = message;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }
 public ErrorLog(HttpStatus status, String message, String details) {
        this.statusCode = status.value();
        this.message = message;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }
}
