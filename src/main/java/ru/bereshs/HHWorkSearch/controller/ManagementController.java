package ru.bereshs.HHWorkSearch.controller;

import com.github.scribejava.core.model.OAuth2AccessToken;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.bereshs.HHWorkSearch.domain.MessageEntity;
import ru.bereshs.HHWorkSearch.domain.SkillEntity;
import ru.bereshs.HHWorkSearch.exception.HhWorkSearchException;
import ru.bereshs.HHWorkSearch.hhApiClient.dto.*;
import ru.bereshs.HHWorkSearch.producer.KafkaProducer;
import ru.bereshs.HHWorkSearch.service.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@Slf4j
@AllArgsConstructor
@Tag(   name = "Отклики",
        description = "Работа с откликами")

public class ManagementController {
    private final HhService service;
    private final AuthorizationService authorizationService;
    private final NegotiationsService messageEntityService;
    private final SkillsEntityService skillsEntityService;
    private final KafkaProducer producer;


    @Operation(summary = "TODO: Тестовая страница, находится в стадии реализации")

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

    @Operation(summary = "Получение списка откликов")
    @GetMapping("/api/negotiations")
    public HhListDto<HhNegotiationsDto> getNegotiationsList() throws IOException, ExecutionException, InterruptedException {
        return service.getHhNegotiationsDtoList(getToken());
    }

    private OAuth2AccessToken getToken() throws IOException, ExecutionException, InterruptedException {
        return authorizationService.getToken();
    }

}
