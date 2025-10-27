package com.example.data_collector.exception;

import com.example.data_collector.dto.ErrorResponse;
import org.apache.kafka.common.errors.SerializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.kafka.KafkaException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * Обработчик исключений для REST API.
 * Обеспечивает единообразный формат ответов при ошибках валидации, Kafka и сервера.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError("Ошибка валидации входных данных");
        String errorString = String.join("; ", errors);
        errorResponse.setMessage("Проверьте корректность передаваемых параметров: " + errorString);

        log.info("Ошибки валидации входных данных: {}", errorString);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SerializationException.class)
    public ResponseEntity<ErrorResponse> handleSerializationException(SerializationException ex) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError("Ошибка преобразования данных");
        errorResponse.setMessage("Не удалось обработать передаваемые данные. Проверьте формат и повторите попытку");

        log.info("Ошибка сериализации данных: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(KafkaException.class)
    public ResponseEntity<ErrorResponse> handleKafkaErrors(KafkaException ex) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError("Ошибка обработки сообщения");
        errorResponse.setMessage("Временные проблемы с обработкой данных. Пожалуйста, повторите запрос позже");

        log.error("Ошибка Kafka: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRequestBody(HttpMessageNotReadableException ex) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError("Некорректный формат запроса");
        errorResponse.setMessage("Запрос содержит синтаксические ошибки или не соответствует ожидаемому формату");

        log.warn("Получен некорректный запрос: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError("Внутренняя ошибка на сервере");
        errorResponse.setMessage("Произошла непредвиденная ошибка");

        log.error("Внутренняя ошибка на сервере: {}", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}