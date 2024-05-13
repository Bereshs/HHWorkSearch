package ru.bereshs.HHWorkSearch.service;

import com.github.scribejava.core.model.OAuth2AccessToken;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.bereshs.HHWorkSearch.domain.dto.TelegramMessageDto;
import ru.bereshs.HHWorkSearch.producer.KafkaProducer;
import ru.bereshs.HHWorkSearch.producer.KafkaProducerImpl;
import ru.bereshs.HHWorkSearch.repository.MessageEntityRepository;
import ru.bereshs.HHWorkSearch.domain.FilteredVacancy;
import ru.bereshs.HHWorkSearch.domain.MessageEntity;
import ru.bereshs.HHWorkSearch.domain.SkillEntity;
import ru.bereshs.HHWorkSearch.exception.HhWorkSearchException;
import ru.bereshs.HHWorkSearch.hhApiClient.dto.HhVacancyDto;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
@AllArgsConstructor
@Slf4j
public class NegotiationsService {

    private final HhService service;
    private final AuthorizationService authorizationService;
    private final MessageEntityRepository messageEntityRepository;
    private final KafkaProducer producer;


    public void doNegotiationWithRelevantVacancy(HhVacancyDto vacancy, String resumeHhid, List<SkillEntity> skills) {
        doNegotiation(getNegotiationMessage(vacancy, skills), resumeHhid, vacancy.getId());
    }

    public void doNegotiation(String message, String resumeId, String vacancyId) {
        try {
            HashMap<String, String> body = getNegotiationBody(message, resumeId, vacancyId);
            log.info("building post negotiation request resumeId: " + resumeId + " vacancyId: " + vacancyId + " message size: " + message.length());
            var result = service.postNegotiation(getToken(), body);
            if(!result.isSuccessful()) {
                String text = "Необходимо участие vacancy Id:"+vacancyId;
                producer.produceDefault(text);
              }
        } catch (IOException | ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private HashMap<String, String> getNegotiationBody(String message, String resumeId, String vacancyId) {
        HashMap<String, String> body = new HashMap<>();
        body.put("message", message);
        body.put("resume_id", resumeId);
        body.put("vacancy_id", vacancyId);
        return body;
    }

    private OAuth2AccessToken getToken() throws IOException, ExecutionException, InterruptedException {
        return authorizationService.getToken();
    }

    public String getNegotiationMessage(FilteredVacancy vacancy, List<SkillEntity> skills) {
        MessageEntity message = getMessage(1);
        if (skills.isEmpty()) {
            message = getMessage(2);
        }
        return message.getMessage(skills, vacancy.getName());
    }

    private MessageEntity getMessage(long id) {
        try {
            return getMessageById(id);
        } catch (HhWorkSearchException e) {
            throw new RuntimeException(e);
        }
    }

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
