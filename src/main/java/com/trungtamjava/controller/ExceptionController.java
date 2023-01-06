package com.trungtamjava.controller;

import com.trungtamjava.dto.ResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.NoResultException;
import java.util.List;

@RestControllerAdvice
public class ExceptionController {
    // log, slf4j
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler({NoResultException.class})
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public ResponseDTO<Void> noResult(NoResultException ex) {
        logger.info("ex: ", ex);
        return ResponseDTO.<Void>builder().status(404).msg("Not Found").build();// view
    }

    // bat loi trung username
    @ExceptionHandler({DataIntegrityViolationException.class})
    @ResponseStatus(code = HttpStatus.CONFLICT)
    public ResponseDTO<Void> conflict(Exception ex) {
        logger.info("ex: ", ex);
        return ResponseDTO.<Void>builder().status(409).msg("CONFLICT").build();// view
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ResponseDTO<Void> badInput(MethodArgumentNotValidException ex) {
        List<ObjectError> errors = ex.getBindingResult().getAllErrors();

        String msg = "";
        for (ObjectError e : errors) {
            FieldError fieldError = (FieldError) e;

            msg += fieldError.getField() + ":" + e.getDefaultMessage() + ";";
        }

        return ResponseDTO.<Void>builder().status(400).msg(msg).build();// view
    }

    @ExceptionHandler({Exception.class})
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseDTO<Void> exception(Exception ex) {
        logger.error("ex: ", ex);
        return ResponseDTO.<Void>builder().status(500).msg("SERVER ERROR").build();// view
    }
}
