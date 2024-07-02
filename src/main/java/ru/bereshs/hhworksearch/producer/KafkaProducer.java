package ru.bereshs.hhworksearch.producer;

import ru.bereshs.hhworksearch.aop.Loggable;
import ru.bereshs.hhworksearch.domain.dto.TelegramMessageDto;

public interface KafkaProducer {
    @Loggable
    void produce(TelegramMessageDto telegramMessageDto);

    @Loggable
    void produceDefault(String text);
}
