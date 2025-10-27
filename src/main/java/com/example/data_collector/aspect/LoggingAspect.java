package com.example.data_collector.aspect;

import com.example.data_collector.dto.DroneDto;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Аспект для логирования работы REST контроллера и Kafka продюсера.
 */
@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    @AfterReturning(pointcut = "execution(* com.example..KafkaProducer.sendDroneData(..)) && args(droneDto)")
    public void logKafkaSuccess(DroneDto droneDto) {
        log.info("БПЛА отправлен в Kafka - Модель: '{}', Устройство: '{}'",
                droneDto.getModel(),
                droneDto.getDetectedBy());
    }

    @Around("@annotation(org.springframework.web.bind.annotation.PostMapping)")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("Начало обработки запроса БПЛА");
        Object result = joinPoint.proceed();
        log.info("Запрос БПЛА обработан успешно");
        return result;
    }
}