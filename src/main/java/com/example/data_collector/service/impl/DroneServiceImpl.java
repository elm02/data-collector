package com.example.data_collector.service.impl;

import com.example.data_collector.dto.DroneDto;
import com.example.data_collector.kafka.KafkaProducer;
import com.example.data_collector.service.DroneService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DroneServiceImpl implements DroneService {

    private final KafkaProducer kafkaProducer;

    public DroneServiceImpl(KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    public Map<String, String> sendToKafka(DroneDto droneDto) {
        String result = kafkaProducer.sendDroneData(droneDto);
        return Map.of(
                "message", result
        );
    }
}