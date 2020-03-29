package ee.suveulikool.netgroup.demo.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorModel {

    private HttpStatus httpStatus;
    private LocalDateTime timestamp;
    private String message;
    private String details;

    public ErrorModel(HttpStatus status, String details, String message) {
        this.httpStatus = status;
        this.details = details;
        this.message = message;
    }
}