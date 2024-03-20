package ru.bereshs.HHWorkSearch.service;

import com.github.scribejava.core.model.OAuth2AccessToken;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.bereshs.HHWorkSearch.Repository.MessageEntityRepository;
import ru.bereshs.HHWorkSearch.domain.FilteredVacancy;
import ru.bereshs.HHWorkSearch.domain.MessageEntity;
import ru.bereshs.HHWorkSearch.domain.SkillEntity;
import ru.bereshs.HHWorkSearch.domain.VacancyEntity;
import ru.bereshs.HHWorkSearch.domain.dto.NegotiationsDto;
import ru.bereshs.HHWorkSearch.exception.HhWorkSearchException;
import ru.bereshs.HHWorkSearch.hhApiClient.HeadHunterClient;

import java.io.IOException;
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

    public void doNegotiationWithRelevantVacancies(List<VacancyEntity> filtered, String resumeHhid, List<SkillEntity> skills) {
        filtered.forEach(vacancy -> {
            NegotiationsDto negotiationsDto = NegotiationsDto.builder()
                    .vacancyId(vacancy.getHhId())
                    .resumeId(resumeHhid)
                    .message(getNegotiationMessage(vacancy, skills))
                    .build();

            doNegotiation(negotiationsDto);
        });

    }

    public void doNegotiation(NegotiationsDto negotiationsDto) {
        try {
            HashMap<String, String> body = getNegotiationBody(negotiationsDto);
            service.postNegotiation(getToken(), body);
        } catch (IOException | ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private HashMap<String, String> getNegotiationBody(NegotiationsDto negotiationsDto) {
        HashMap<String, String> body = new HashMap<>();
        body.put("message", negotiationsDto.getMessage());
        body.put("resume_id", negotiationsDto.getResumeId());
        body.put("vacancy_id", negotiationsDto.getVacancyId());
        return body;
    }

    private OAuth2AccessToken getToken() throws IOException, ExecutionException, InterruptedException {
        return authorizationService.getToken();
    }

    public String getNegotiationMessage(FilteredVacancy vacancy, List<SkillEntity> skills)  {
        MessageEntity message = getMessage(1);
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
