package com.example.data_collector.dto;

import jakarta.validation.constraints.*;

/**
 * DTO для передачи данных о БПЛА.
 * Содержит информацию о модели, координатах, скорости и устройстве обнаружения.
 */
public class DroneDto {

    @NotBlank(message = "Модель БПЛА не может быть пустой")
    private String model;

    @NotNull(message = "Скорость БПЛА обязательна")
    @Positive(message = "Скорость должна быть положительной")
    @Max(value = 600, message = "Скорость должна быть меньше 600 км/ч")
    private Double speed;

    @NotNull(message = "Широта обязательна")
    @DecimalMin(value = "-90.0", message = "Широта не может быть меньше -90")
    @DecimalMax(value = "90.0", message = "Широта не может быть больше 90")
    private Double latitude;

    @NotNull(message = "Долгота обязательна")
    @DecimalMin(value = "-180.0", message = "Долгота не может быть меньше -180")
    @DecimalMax(value = "180.0", message = "Долгота не может быть больше 180")
    private Double longitude;

    @NotNull(message = "Высота полёта обязательна")
    @Positive(message = "Высота полёта должна быть положительной")
    @Max(value = 10_000, message = "Высота полёта не может быть больше 10 000 метров")
    private Double flightAltitude;

    @NotBlank(message = "Устройство обнаружения не может быть пустым")
    private String detectedBy;

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setFlightAltitude(Double flightAltitude) {
        this.flightAltitude = flightAltitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setDetectedBy(String detectedBy) {
        this.detectedBy = detectedBy;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public String getModel() {
        return model;
    }

    public Double getSpeed() {
        return speed;
    }

    public Double getFlightAltitude() {
        return flightAltitude;
    }

    public String getDetectedBy() {
        return detectedBy;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}