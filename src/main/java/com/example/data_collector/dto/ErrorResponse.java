package com.example.data_collector.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Стандартизированный формат ответа об ошибках.
 * Содержит UUID ошибки и временную метку для отслеживания.
 */
public class ErrorResponse {

    private String error;

    private final UUID uuid;

    private final LocalDateTime time;

    private String message;

    public ErrorResponse(){
        this.uuid = UUID.randomUUID();
        this.time = LocalDateTime.now();
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTime() {
        return time;
    }
}