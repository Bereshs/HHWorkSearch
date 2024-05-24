package ru.bereshs.hhworksearch.controller;

import com.github.scribejava.core.model.OAuth2AccessToken;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.bereshs.hhworksearch.domain.MessageEntity;
import ru.bereshs.hhworksearch.domain.SkillEntity;
import ru.bereshs.hhworksearch.domain.VacancyEntity;
import ru.bereshs.hhworksearch.exception.HhWorkSearchException;
import ru.bereshs.hhworksearch.hhApiClient.dto.*;
import ru.bereshs.hhworksearch.producer.KafkaProducer;
import ru.bereshs.hhworksearch.service.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@Slf4j
@AllArgsConstructor
@Tag(name = "Отклики",
        description = "Работа с откликами")

public class ManagementController {
    private final HhService service;
    private final AuthorizationService authorizationService;
    private final NegotiationsService negotiationsService;
    private final SkillsEntityService skillsEntityService;
    private final VacancyEntityService vacancyEntityService;
    private final KafkaProducer kafkaProducer;
    private final SettingsService settingsService;


    @Operation(summary = "TODO: Тестовая страница, находится в стадии реализации")

    @GetMapping("/api/start")
    public String start() throws HhWorkSearchException, InterruptedException {

        HhVacancyDto vacancy = getVacancyById("94994056");

        log.info("vacancy:" + vacancy);
        List<SkillEntity> skills = skillsEntityService.extractVacancySkills(vacancy);
        log.info("skills:" + skills);
        MessageEntity message = negotiationsService.getMessageById(1);

        log.info("message:" + message.getMessage(skills, vacancy.getName()));
        return vacancy.toString();
    }


    public HhVacancyDto getVacancyById(String id) throws InterruptedException {
        try {
            return service.getVacancyById(id, getToken());
        } catch (IOException | ExecutionException  e) {
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

    @Operation(summary = "Обработка сообщений")
    @PostMapping("/api/negotiations")
    public String updateNegotiations() throws IOException, ExecutionException, InterruptedException {
        System.out.println("eeeee");
        var negotiationsList = service.getHhNegotiationsDtoList(getToken());

        List<VacancyEntity> vacancyList = negotiationsList.getItems().stream().map(entity -> {
            VacancyEntity vacancy = new VacancyEntity(entity.getVacancy());
            vacancy.setStatus(entity.getState().getId());
            return vacancy;
        }).toList();
        vacancyEntityService.updateVacancyStatusFromList(vacancyList);

        return "ok";
    }

    @Operation(summary = "Ежедневный отчет")
    @GetMapping("/api/negotiations/daily")
    public String dailyReport() {

        String message = vacancyEntityService.getDaily();
//        TelegramMessageDto messageDto = new TelegramMessageDto(settingsService.getAppTelegramToken(), settingsService.getAppClientId(), message, LocalDateTime.now());
//        kafkaProducer.produce(messageDto);

        kafkaProducer.produceDefault(message);
        return message;
    }


}
