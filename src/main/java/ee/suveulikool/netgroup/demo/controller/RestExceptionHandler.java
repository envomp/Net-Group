package ee.suveulikool.netgroup.demo.controller;

import ee.suveulikool.netgroup.demo.exception.PersonExistsException;
import ee.suveulikool.netgroup.demo.exception.PersonIsCutException;
import ee.suveulikool.netgroup.demo.exception.PersonNotFoundException;
import ee.suveulikool.netgroup.demo.exception.PersonValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler { // Make server return 40x and 50x respectively

    @ExceptionHandler(PersonExistsException.class)
    private ResponseEntity<ErrorModel> handlePersonExistsException(PersonExistsException ex) {
        ErrorModel error = new ErrorModel(HttpStatus.CONFLICT, "Entity already exists", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(PersonIsCutException.class)
    private ResponseEntity<ErrorModel> handlePersonIsCutException(PersonIsCutException ex) {
        ErrorModel error = new ErrorModel(HttpStatus.INTERNAL_SERVER_ERROR, "Server failed with in place modification", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR); // never should actually get here
    }

    @ExceptionHandler(PersonNotFoundException.class)
    private ResponseEntity<ErrorModel> handlePersonNotFoundException(PersonNotFoundException ex) {
        ErrorModel error = new ErrorModel(HttpStatus.NOT_FOUND, "Entity does not exist", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PersonValidationException.class)
    private ResponseEntity<ErrorModel> handlePersonValidationException(PersonValidationException ex) {
        ErrorModel error = new ErrorModel(HttpStatus.BAD_REQUEST, "Entity does not meet the required standards", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
