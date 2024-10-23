package com.group8.projectmanager.controllers;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionsController {

    @ExceptionHandler(EntityNotFoundException.class)
    public ProblemDetail handleNotFound(EntityNotFoundException e) {

        var response = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        response.setDetail(e.getMessage());

        return response;
    }

    @ExceptionHandler(ErrorResponseException.class)
    public ErrorResponse errorResponse(ErrorResponseException e) {
        return e;
    }
}
