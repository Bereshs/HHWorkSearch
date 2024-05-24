package ru.bereshs.hhworksearch.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.bereshs.hhworksearch.domain.dto.TelegramMessageDto;
import ru.bereshs.hhworksearch.service.SettingsService;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaProducerImpl implements KafkaProducer{


    @Value("${spring.kafka.topic}")
    private String kafkaTopic;

    private final KafkaTemplate<Long, TelegramMessageDto> kafkaTemplate;

    private final SettingsService settingsService;

    @Override
    public void produce(TelegramMessageDto telegramMessageDto) {
        kafkaTemplate.send(kafkaTopic, telegramMessageDto);
    }

    public void produceDefault(String text) {
        TelegramMessageDto messageDto = new TelegramMessageDto(settingsService.getAppTelegramToken(), settingsService.getAppClientId(), text, LocalDateTime.now());
        produce(messageDto);
    }
}
