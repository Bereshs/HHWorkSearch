package ru.bereshs.hhworksearch.producer;

import ru.bereshs.hhworksearch.domain.dto.TelegramMessageDto;

public interface KafkaProducer {
    void produce(TelegramMessageDto telegramMessageDto);

    void produceDefault(String text);
}
