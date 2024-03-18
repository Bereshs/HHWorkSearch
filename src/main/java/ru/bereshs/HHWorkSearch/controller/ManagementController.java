package ru.bereshs.HHWorkSearch.controller;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.Verb;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import ru.bereshs.HHWorkSearch.config.AppConfig;
import ru.bereshs.HHWorkSearch.domain.MessageEntity;
import ru.bereshs.HHWorkSearch.domain.SkillEntity;
import ru.bereshs.HHWorkSearch.domain.VacancyEntity;
import ru.bereshs.HHWorkSearch.domain.dto.NegotiationsDto;
import ru.bereshs.HHWorkSearch.domain.dto.TelegramMessageDto;
import ru.bereshs.HHWorkSearch.exception.HhWorkSearchException;
import ru.bereshs.HHWorkSearch.hhApiClient.HeadHunterClient;
import ru.bereshs.HHWorkSearch.hhApiClient.dto.*;
import ru.bereshs.HHWorkSearch.producer.KafkaProducer;
import ru.bereshs.HHWorkSearch.service.*;
import ru.bereshs.HHWorkSearch.domain.KeyEntity;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

@RestController
@Slf4j
@AllArgsConstructor
public class ManagementController {
    private final HhService service;
    private final AuthorizationService authorizationService;
    private final FilterEntityService filterEntityService;
    private final VacancyEntityService vacancyEntityService;
    private final MessageEntityService messageEntityService;
    private final SkillsEntityService skillsEntityService;
    private final KafkaProducer producer;


    @GetMapping("/api/start")
    public String start() throws IOException, ExecutionException, InterruptedException, HhWorkSearchException {

       HhVacancyDto vacancy = getVacancyById("94994056");

       log.info("vacancy:"+vacancy);
       List<SkillEntity> skills = skillsEntityService.extractVacancySkills(vacancy);
       log.info("skills:"+skills);
       MessageEntity message = messageEntityService.getMessageById(1);

       log.info("message:"+message.getMessage(skills,vacancy.getName()));
           return vacancy.toString() ;
    }


    public HhVacancyDto getVacancyById(String id) {
        try {
            return service.getVacancyById(id, getToken());
        } catch (IOException | ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/api/negotiations")
    public HhListDto<HhNegotiationsDto> getNegotiationsList() throws IOException, ExecutionException, InterruptedException {
        return service.getHhNegotiationsDtoList(getToken());
    }

    @PostMapping("/api/negotiations")
    public String postNegotiations(@RequestBody NegotiationsDto negotiationsDto) throws IOException, ExecutionException, InterruptedException, HhWorkSearchException {
        HhVacancyDto vacancyDto = service.getVacancyById(negotiationsDto.getVacancyId(), getToken());
        MessageEntity message = messageEntityService.getMessageById(1);
        List<SkillEntity> skills = skillsEntityService.getSkillEntityList(vacancyDto);

        negotiationsDto.setMessage(message.getMessage(
                skillsEntityService.updateList(skills),
                vacancyDto.getName()));
/*
        String uri = "https://api.hh.ru/negotiations";
        HashMap<String, String> body = new HashMap<>();
        body.put("message", negotiationsDto.getMessage());
        body.put("resume_id", negotiationsDto.getResumeId());
        body.put("vacancy_id", negotiationsDto.getVacancyId());

        var result =client.executeWithBody(Verb.POST, uri, getToken(), body);
        Logger.getLogger("result code="+result.getCode());

        Logger.getLogger("result code="+result.getHeaders());
  */
        log.info("rating=" + skillsEntityService.getRating(vacancyDto));
        return negotiationsDto.getMessage();
    }

    private OAuth2AccessToken getToken() throws IOException, ExecutionException, InterruptedException {
        return authorizationService.getToken();
    }

}
