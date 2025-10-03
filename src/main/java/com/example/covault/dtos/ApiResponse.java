package com.example.covault.dtos;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.Instant;

@Data
@Setter
@Getter
@NoArgsConstructor
public class ApiResponse<T> {
    private String message;
    private HttpStatus status;
    private T Data;
    private String timeStamp = Instant.now().toString();

    public ApiResponse(String message, HttpStatus status, T Data) {
        this.message = message;
        this.status = status;
        this.Data = Data;
    }
}
