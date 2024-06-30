package com.psybrainy.CallFlowManager.call.adapter.exception;

import com.psybrainy.CallFlowManager.share.AbstractLogger;
import com.psybrainy.CallFlowManager.share.exception.CallHandlingException;
import com.psybrainy.CallFlowManager.share.exception.EmployeeServiceException;
import com.psybrainy.CallFlowManager.share.exception.KafkaMessageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends AbstractLogger {

    @ExceptionHandler(KafkaMessageException.class)
    public ResponseEntity<String> handleKafkaMessageException(KafkaMessageException ex) {
        log.error("Kafka message exception: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmployeeServiceException.class)
    public ResponseEntity<String> handleEmployeeServiceException(EmployeeServiceException ex) {
        log.error("Call processing exception: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(CallHandlingException.class)
    public ResponseEntity<String> handleCallHandlingException(CallHandlingException ex) {
        log.error("Call handling exception: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
