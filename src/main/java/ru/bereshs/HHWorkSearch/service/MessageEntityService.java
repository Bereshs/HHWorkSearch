package ru.bereshs.HHWorkSearch.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.bereshs.HHWorkSearch.Repository.MessageEntityRepository;
import ru.bereshs.HHWorkSearch.domain.MessageEntity;
import ru.bereshs.HHWorkSearch.exception.HhWorkSearchException;

@Service
@AllArgsConstructor
public class MessageEntityService {
    private final MessageEntityRepository messageEntityRepository;

    public MessageEntity getMessageById(long id) throws HhWorkSearchException {
        return messageEntityRepository.findById(id).orElseThrow(() -> new HhWorkSearchException("Wrong message id"));
    }

    public void save(MessageEntity messageEntity) {
        messageEntityRepository.save(messageEntity);
    }

    public void patchMessageById(long id, MessageEntity messageDto) throws HhWorkSearchException {
        MessageEntity message = getMessageById(id);
        message.setHeader(messageDto.getHeader());
        message.setFooter(messageDto.getFooter());
        save(message);
    }
}
