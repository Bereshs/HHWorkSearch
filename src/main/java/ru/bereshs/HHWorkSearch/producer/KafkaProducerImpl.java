package ru.bereshs.HHWorkSearch.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.bereshs.HHWorkSearch.domain.dto.TelegramMessageDto;

@Service
@Slf4j
public class KafkaProducerImpl implements KafkaProducer{

    @Value("${spring.kafka.topic}")
    private String kafkaTopic;

    private final KafkaTemplate<Long, TelegramMessageDto> kafkaTemplate;

    @Autowired
    public KafkaProducerImpl(KafkaTemplate<Long, TelegramMessageDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void produce(TelegramMessageDto telegramMessageDto) {
        kafkaTemplate.send(kafkaTopic, telegramMessageDto);
        log.info("Sent to: "+kafkaTopic+"  message: "+telegramMessageDto);
    }
}
