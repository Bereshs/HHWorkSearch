package ru.bereshs.HHWorkSearch.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.bereshs.HHWorkSearch.domain.dto.TelegramMessageDto;
import ru.bereshs.HHWorkSearch.service.SettingsService;

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
