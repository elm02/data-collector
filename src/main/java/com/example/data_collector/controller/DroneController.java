package com.example.data_collector.controller;

import com.example.data_collector.dto.DroneDto;
import com.example.data_collector.service.DroneService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * REST контроллер для приема данных о БПЛА и отправки в Kafka
 */
@RestController
@RequestMapping("/api/drones")
public class DroneController {

    private final DroneService droneService;

    public DroneController(DroneService droneService) {
        this.droneService = droneService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> receiveDroneData(@Valid @RequestBody DroneDto droneDto) {
        Map<String, String> response = droneService.sendToKafka(droneDto);
        return ResponseEntity.ok(response);
    }
}