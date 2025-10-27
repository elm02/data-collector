package com.example.data_collector.service;

import com.example.data_collector.dto.DroneDto;

import java.util.Map;

/**
 * Сервисный слой для обработки данных БПЛА.
 * Координирует отправку данных в Kafka.
 */
public interface DroneService {

    Map<String, String> sendToKafka(DroneDto droneDto);
}