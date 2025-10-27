package com.example.data_collector;

import com.example.data_collector.aspect.LoggingAspect;
import com.example.data_collector.controller.DroneController;
import com.example.data_collector.dto.DroneDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.kafka.test.context.EmbeddedKafka;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

import java.util.concurrent.CompletableFuture;

@SpringBootTest
@AutoConfigureMockMvc
@EmbeddedKafka(partitions = 1, topics = {"drone-topic"})
@TestPropertySource(properties = {
        "spring.kafka.topic.drone=drone-topic"
})
class DroneControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private KafkaTemplate<String, DroneDto> kafkaTemplate;

    @Autowired
    private DroneController droneController;

    @Autowired
    private LoggingAspect loggingAspect;

    private DroneDto validDroneDto;

    @BeforeEach
    void setUp() {
        validDroneDto = new DroneDto();
        validDroneDto.setModel("Model");
        validDroneDto.setSpeed(50.0);
        validDroneDto.setLatitude(70.0);
        validDroneDto.setLongitude(90.0);
        validDroneDto.setFlightAltitude(120.0);
        validDroneDto.setDetectedBy("DetectedBy");
    }

    @Test
    void whenValidDroneData_thenSuccess() throws Exception {
        SendResult<String, DroneDto> sendResult = mock(SendResult.class);
        CompletableFuture<SendResult<String, DroneDto>> future =
                CompletableFuture.completedFuture(sendResult);

        when(kafkaTemplate.send(any(String.class), any(String.class), any(DroneDto.class)))
                .thenReturn(future);

        mockMvc.perform(post("/api/drones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDroneDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Данные о БПЛА приняты в обработку")));
    }

    @Test
    void whenEmptyModel_thenValidationError() throws Exception {
        validDroneDto.setModel("");

        mockMvc.perform(post("/api/drones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDroneDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Ошибка валидации входных данных")))
                .andExpect(jsonPath("$.message", containsString("Модель БПЛА не может быть пустой")))
                .andExpect(jsonPath("$.uuid").exists())
                .andExpect(jsonPath("$.time").exists());
    }

    @Test
    void whenNegativeSpeed_thenValidationError() throws Exception {
        validDroneDto.setSpeed(-10.0);

        mockMvc.perform(post("/api/drones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDroneDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Ошибка валидации входных данных")))
                .andExpect(jsonPath("$.message", containsString("Скорость должна быть положительной")))
                .andExpect(jsonPath("$.uuid").exists())
                .andExpect(jsonPath("$.time").exists());
    }

    @Test
    void whenSpeedExceedsLimit_thenValidationError() throws Exception {
        validDroneDto.setSpeed(700.0);

        mockMvc.perform(post("/api/drones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDroneDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Ошибка валидации входных данных")))
                .andExpect(jsonPath("$.message", containsString("Скорость должна быть меньше 600 км/ч")))
                .andExpect(jsonPath("$.uuid").exists())
                .andExpect(jsonPath("$.time").exists());
    }

    @Test
    void whenInvalidLatitude_thenValidationError() throws Exception {
        validDroneDto.setLatitude(-100.0);

        mockMvc.perform(post("/api/drones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDroneDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Ошибка валидации входных данных")))
                .andExpect(jsonPath("$.message", containsString("Широта не может быть меньше -90")))
                .andExpect(jsonPath("$.uuid").exists())
                .andExpect(jsonPath("$.time").exists());
    }

    @Test
    void whenInvalidLongitude_thenValidationError() throws Exception {
        validDroneDto.setLongitude(200.0);

        mockMvc.perform(post("/api/drones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDroneDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Ошибка валидации входных данных")))
                .andExpect(jsonPath("$.message", containsString("Долгота не может быть больше 180")))
                .andExpect(jsonPath("$.uuid").exists())
                .andExpect(jsonPath("$.time").exists());
    }

    @Test
    void whenFlightAltitudeExceedsLimit_thenValidationError() throws Exception {
        validDroneDto.setFlightAltitude(15000.0);

        mockMvc.perform(post("/api/drones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDroneDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Ошибка валидации входных данных")))
                .andExpect(jsonPath("$.message", containsString("Высота полёта не может быть больше 10 000 метров")))
                .andExpect(jsonPath("$.uuid").exists())
                .andExpect(jsonPath("$.time").exists());
    }

    @Test
    void whenEmptyDetectedBy_thenValidationError() throws Exception {
        validDroneDto.setDetectedBy("");

        mockMvc.perform(post("/api/drones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDroneDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Ошибка валидации входных данных")))
                .andExpect(jsonPath("$.message", containsString("Устройство обнаружения не может быть пустым")))
                .andExpect(jsonPath("$.uuid").exists())
                .andExpect(jsonPath("$.time").exists());
    }

    @Test
    void whenKafkaTimeout_thenServiceUnavailable() throws Exception {
        CompletableFuture<SendResult<String, DroneDto>> future =
                new CompletableFuture<>();

        when(kafkaTemplate.send(any(String.class), any(String.class), any(DroneDto.class)))
                .thenReturn(future);

        mockMvc.perform(post("/api/drones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDroneDto)))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.error", is("Ошибка обработки сообщения")))
                .andExpect(jsonPath("$.message", is("Временные проблемы с обработкой данных. Пожалуйста, повторите запрос позже")))
                .andExpect(jsonPath("$.uuid").exists())
                .andExpect(jsonPath("$.time").exists());
    }

    @Test
    void whenInvalidJson_thenBadRequest() throws Exception {
        mockMvc.perform(post("/api/drones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json }"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Некорректный формат запроса")))
                .andExpect(jsonPath("$.message", is("Запрос содержит синтаксические ошибки или не соответствует ожидаемому формату")))
                .andExpect(jsonPath("$.uuid").exists())
                .andExpect(jsonPath("$.time").exists());
    }

    @Test
    void whenBoundaryValues_thenSuccess() throws Exception {
        validDroneDto.setSpeed(600.0);
        validDroneDto.setLatitude(90.0);
        validDroneDto.setLongitude(180.0);
        validDroneDto.setFlightAltitude(10000.0);

        SendResult<String, DroneDto> sendResult = mock(SendResult.class);
        CompletableFuture<SendResult<String, DroneDto>> future =
                CompletableFuture.completedFuture(sendResult);

        when(kafkaTemplate.send(any(String.class), any(String.class), any(DroneDto.class)))
                .thenReturn(future);

        mockMvc.perform(post("/api/drones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDroneDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Данные о БПЛА приняты в обработку")));
    }

    @Test
    void whenUnexpectedError_thenInternalServerError() throws Exception {
        when(kafkaTemplate.send(any(String.class), any(String.class), any(DroneDto.class)))
                .thenThrow(new RuntimeException("Error"));

        mockMvc.perform(post("/api/drones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDroneDto)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", is("Внутренняя ошибка на сервере")))
                .andExpect(jsonPath("$.message", is("Произошла непредвиденная ошибка")))
                .andExpect(jsonPath("$.uuid").exists())
                .andExpect(jsonPath("$.time").exists());
    }
}