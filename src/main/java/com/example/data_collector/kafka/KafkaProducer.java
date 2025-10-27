package com.example.data_collector.kafka;

import com.example.data_collector.dto.DroneDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Сервис для отправки данных о БПЛА в Kafka.
 */
@Service
public class KafkaProducer {

    private static final Logger log = LoggerFactory.getLogger(KafkaProducer.class);

    @Value("${spring.kafka.topic.drone}")
    private String topic;

    @Autowired
    private KafkaTemplate<String, DroneDto> kafkaTemplate;

    public String sendDroneData(DroneDto droneDto) {
        try {
            CompletableFuture<SendResult<String, DroneDto>> future =
                    kafkaTemplate.send(topic, droneDto.getDetectedBy(), droneDto);

            SendResult<String, DroneDto> result = future.get(5, TimeUnit.SECONDS);

            return "Данные о БПЛА приняты в обработку";

        } catch (InterruptedException ex) {
            throw new KafkaException("Прервана отправка в Kafka", ex);
        } catch (TimeoutException ex) {
            throw new KafkaException("Таймаут отправки в Kafka", ex);
        } catch (ExecutionException ex) {
            throw new KafkaException("Ошибка отправки в Kafka", ex);
        }
    }
}