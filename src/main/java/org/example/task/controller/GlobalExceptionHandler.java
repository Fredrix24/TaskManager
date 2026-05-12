package org.example.task.controller;

import org.example.task.exception.TaskNotFoundException;
import org.example.task.dto.ErrorResponse;
import org.example.task.dto.ValidationErrorDto;
import org.example.task.dto.FieldErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //Обработка ошибок валидации
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorDto> handleValidationException(
            MethodArgumentNotValidException ex) {
        //Получение списка ошибок
        List<FieldErrorDto> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new FieldErrorDto(
                        error.getField(),
                        error.getDefaultMessage()
                ))
                .collect(Collectors.toList());
        //Форматирование ответа
        ValidationErrorDto response = new ValidationErrorDto(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                errors
        );

        return ResponseEntity.badRequest().body(response);
    }
    //Обработка ошибки "Задача не найдена"
    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTaskNotFound(TaskNotFoundException ex) {
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Task not found",
                "/tasks/" + ex.getTaskId()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}