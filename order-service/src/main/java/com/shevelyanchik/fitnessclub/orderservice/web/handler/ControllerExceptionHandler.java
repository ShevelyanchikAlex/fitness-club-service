package com.shevelyanchik.fitnessclub.orderservice.web.handler;

import com.shevelyanchik.fitnessclub.orderservice.service.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Locale;

@ResponseBody
@ControllerAdvice
@RequiredArgsConstructor
public class ControllerExceptionHandler {
    private static final String BAD_REQUEST_MESSAGE_KEY = "bad.request";
    private final MessageSource messageSource;

    @ExceptionHandler(ServiceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleServiceException(ServiceException exception) {
        String errorMessage = messageSource.getMessage(exception.getMessage(), new Object[]{}, Locale.ENGLISH);
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleIllegalArgumentException() {
        String errorMessage = messageSource.getMessage(BAD_REQUEST_MESSAGE_KEY, new Object[]{}, Locale.ENGLISH);
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }
}
