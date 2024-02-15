package ru.bereshs.HHWorkSearch.producer;

import ru.bereshs.HHWorkSearch.domain.dto.TelegramMessageDto;

public interface KafkaProducer {
    void produce(TelegramMessageDto telegramMessageDto);
}
