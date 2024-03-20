package ru.bereshs.HHWorkSearch.config;

import com.github.scribejava.core.model.OAuth2AccessToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import ru.bereshs.HHWorkSearch.domain.*;
import ru.bereshs.HHWorkSearch.domain.dto.TelegramMessageDto;
import ru.bereshs.HHWorkSearch.hhApiClient.dto.HhVacancyDto;
import ru.bereshs.HHWorkSearch.producer.KafkaProducer;
import ru.bereshs.HHWorkSearch.service.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Configuration
@EnableScheduling
@Slf4j
public class SchedulerConfig {

    @Value("${app.telegram.token}")
    private String token;
    @Value("${app.clientId}")
    private String clientId;
    private final VacancyEntityService vacancyEntityService;
    private final AuthorizationService authorizationService;
    private final HhService service;
    private final FilterEntityService<VacancyEntity> filterEntityService;
    private final KafkaProducer producer;

    private final SkillsEntityService skillsEntityService;
    private final ResumeEntityService resumeEntityService;
    private final NegotiationsService negotiationsService;

    @Autowired
    public SchedulerConfig(VacancyEntityService vacancyEntityService, AuthorizationService authorizationService, HhService service, FilterEntityService<VacancyEntity> filterEntityService, KafkaProducer kafkaProducer, SkillsEntityService skillsEntityService, ResumeEntityService resumeEntityService, NegotiationsService negotiationsService) {
        this.vacancyEntityService = vacancyEntityService;
        this.authorizationService = authorizationService;
        this.service = service;
        this.filterEntityService = filterEntityService;
        this.producer = kafkaProducer;
        this.skillsEntityService = skillsEntityService;
        this.resumeEntityService = resumeEntityService;
        this.negotiationsService = negotiationsService;
    }

    @Scheduled(cron = "0 0 9-18 * * *")
    public void scheduleDayLightTask() {
        log.info("starting scheduled task");
        sendMessageWithRelevantVacancies();
    }

    private void sendMessageWithRelevantVacancies() {
        var filtered = getRelevantVacancies();
        vacancyEntityService.saveAll(filtered);
        produceKafkaMessage(filtered);
        vacancyEntityService.changeStatus(filtered, VacancyStatus.view);
    }

    private void postNegotiationWithRelevantVacancies() {
        var filtered = getRelevantVacancies();
        vacancyEntityService.saveAll(filtered);
        postNegotiations(filtered);
        vacancyEntityService.changeStatus(filtered, VacancyStatus.request);
    }

    private void postNegotiations(List<VacancyEntity> filtered) {
        filtered.forEach(vacancy -> {
            ResumeEntity resume = resumeEntityService.getRelevantResume(vacancy);
            List<SkillEntity> skills = skillsEntityService.extractVacancySkills(vacancy);
            negotiationsService.doNegotiationWithRelevantVacancies(filtered, resume.getHhId(), skills);
        });

    }

    private List<VacancyEntity> getRelevantVacancies() {
        List<HhVacancyDto> vacancyList = getHhVacancy();
        List<VacancyEntity> vacancies = vacancyEntityService.getVacancyEnityList(vacancyList);
        log.info("total received vacancy list size:  " + vacancies.size());
        List<VacancyEntity> filtered = filterEntityService.doFilter(vacancies);
        log.info("total size vacancy after name filter: " + filtered.size());
        List<HhVacancyDto> list = getUnique(filtered);
        filtered = filterEntityService.doFilterDescription(vacancyEntityService.getVacancyEnityList(list));
        log.info("total size after full filter: " + filtered.size());
        return filtered;
    }


    private void produceKafkaMessage(List<VacancyEntity> list) {
        list.forEach(element -> {
            String message = element.getName() + "\n"
                    + "message: " + negotiationsService.getNegotiationMessage(element, skillsEntityService.extractVacancySkills(element)) + "\n"
                    + element.getUrl();
            TelegramMessageDto messageDto = new TelegramMessageDto(token, clientId, message, LocalDateTime.now());
            producer.produce(messageDto);
        });

    }


    private List<HhVacancyDto> getUnique(List<VacancyEntity> list) {
        return vacancyEntityService.getUnique(list).stream().map(element -> getVacancyById(element.getHhId())).toList();
    }

    public HhVacancyDto getVacancyById(String id) {
        try {
            return service.getVacancyById(id, getToken());
        } catch (IOException | ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private OAuth2AccessToken getToken() {
        try {
            return authorizationService.getToken();
        } catch (IOException | ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private List<HhVacancyDto> getHhVacancy() {
        LocalDateTime lastUpdate = vacancyEntityService.getLastUpdate();
        boolean isDayAfterFullRequest = LocalDateTime.now().minusDays(1).isAfter(lastUpdate);

        if (isDayAfterFullRequest) {
            log.info("request full");
            return getRecommendedVacancy();
        }
        return getPageRecommendedVacancy();
    }

    private List<HhVacancyDto> getRecommendedVacancy() {
        try {
            return service.getRecommendedVacancy(getToken());
        } catch (IOException | ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private List<HhVacancyDto> getPageRecommendedVacancy() {
        try {
            return service.getPageRecommendedVacancy(getToken(), 0).getItems();
        } catch (IOException | ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
