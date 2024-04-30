package ru.bereshs.HHWorkSearch.config;

import com.github.scribejava.core.model.OAuth2AccessToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import ru.bereshs.HHWorkSearch.domain.*;
import ru.bereshs.HHWorkSearch.domain.dto.TelegramMessageDto;
import ru.bereshs.HHWorkSearch.hhApiClient.HhLocalDateTime;
import ru.bereshs.HHWorkSearch.hhApiClient.dto.HhVacancyDto;
import ru.bereshs.HHWorkSearch.producer.KafkaProducer;
import ru.bereshs.HHWorkSearch.service.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class SchedulerConfig {

    private final VacancyEntityService vacancyEntityService;
    private final AuthorizationService authorizationService;
    private final HhService service;
    private final FilterEntityService<HhVacancyDto> filterEntityService;
    private final KafkaProducer producer;

    private final SkillsEntityService skillsEntityService;
    private final ResumeEntityService resumeEntityService;
    private final NegotiationsService negotiationsService;
    private final SettingsService settingsService;
    private final EmployerEntityService employerEntityService;


    @Scheduled(cron = "0 11 9-18 * * *")
    public void scheduleDayLightTask() throws IOException, ExecutionException, InterruptedException {
        if (settingsService.isDemonActive()) {
            log.info("starting scheduled task");
            List<HhVacancyDto> vacancyList = getHhVacancy(getKey());
            //    sendMessageWithRelevantVacancies(vacancyList);
            postNegotiationWithRelevantVacancies(vacancyList);
            updateResume();
        }
    }

    @Scheduled(cron = "0 30 19 * * *")
    public void scheduleDailyFullRequest() {
        if (settingsService.isDemonActive()) {
            log.info("request full hhVacancies");
            List<HhVacancyDto> vacancyList = getFullHhVacancy();
            //    sendMessageWithRelevantVacancies(vacancyList);
            postNegotiationWithRelevantVacancies(vacancyList);
        }
    }

    @Scheduled(cron = "0 30 18 * * *")
    public void scheduleDailyRecommendedRequest() throws IOException, ExecutionException, InterruptedException {
        if (settingsService.isDemonActive()) {
            log.info("request recommended hhVacancies");
            List<HhVacancyDto> vacancyList = service.getPageRecommendedVacancyForResume(getToken(), resumeEntityService.getDefault()).getItems();
            //    sendMessageWithRelevantVacancies(vacancyList);
            postNegotiationWithRelevantVacancies(vacancyList);
        }
    }


    private void sendMessageWithRelevantVacancies(List<HhVacancyDto> vacancyList) {
        var filtered = getRelevantVacancies(vacancyList);
        vacancyEntityService.saveAll(filtered);
        produceKafkaMessage(filtered);
        vacancyEntityService.changeAllStatus(filtered, VacancyStatus.view);
    }

    private void postNegotiationWithRelevantVacancies(List<HhVacancyDto> vacancyList) {
        var filtered = getRelevantVacancies(vacancyList);
        vacancyEntityService.saveAll(filtered);
        postNegotiations(filtered);
        vacancyEntityService.changeAllStatus(filtered, VacancyStatus.request);
    }


    private void updateResume() throws IOException, ExecutionException, InterruptedException {

        ResumeEntity resume = resumeEntityService.getDefault();
        if (resume.getNextPublish() == null) {
            var resumeDto = service.getResumeById(resume.getHhId(), getToken());
            resume.setNextPublish(HhLocalDateTime.decodeLocalData(resumeDto.getNextPublishAt()));
        }

        if (resume.getNextPublish().isBefore(LocalDateTime.now())) {
            service.updateResume(getToken(), resume.getHhId());
        }

        var resumeDto = service.getResumeById(resume.getHhId(), getToken());
        resume.setNextPublish(HhLocalDateTime.decodeLocalData(resumeDto.getNextPublishAt()));
        resumeEntityService.save(resume);

    }

    private void postNegotiations(List<HhVacancyDto> filtered) {
        filtered.forEach(vacancy -> {
            VacancyEntity vacancyEntity = vacancyEntityService.getByVacancyDto(vacancy);
            if (!vacancyEntity.getStatus().equals(VacancyStatus.request)) {
                ResumeEntity resume = resumeEntityService.getRelevantResume(vacancy);
                List<SkillEntity> skills = skillsEntityService.extractVacancySkills(vacancy);
                negotiationsService.doNegotiationWithRelevantVacancy(vacancy, resume.getHhId(), skills);
            }
        });
    }

    private List<HhVacancyDto> getRelevantVacancies(List<HhVacancyDto> vacancyList) {
        log.info("total received vacancy list size:  " + vacancyList.size());
        List<HhVacancyDto> filtered = filterEntityService.doFilterNameAndExperience(vacancyList);
        log.info("total size vacancy after name filter: " + filtered.size());
        List<HhVacancyDto> list = vacancyEntityService.getUnique(filtered);
        log.info("total size after unique: " + list.size());
        var full = getFullVacancyInformation(filtered);
        vacancyEntityService.updateTimeStamp(full);
        filtered = filterEntityService.doFilterDescription(full);
        log.info("total size after full filter: " + filtered.size());
        return filtered;
    }


    private void produceKafkaMessage(List<HhVacancyDto> list) {
        String token = settingsService.getAppTelegramToken();
        String clientId = settingsService.getAppClientId();
        list.forEach(element -> {
            String message = element.getName() + "\n"
                    + "message: " + negotiationsService.getNegotiationMessage(element, skillsEntityService.extractVacancySkills(element)) + "\n"
                    + element.getUrl();
            TelegramMessageDto messageDto = new TelegramMessageDto(token, clientId, message, LocalDateTime.now());
            producer.produce(messageDto);
        });

    }

    public HhVacancyDto getVacancyById(String id) {
        try {
            return service.getVacancyById(id, getToken());
        } catch (IOException | ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public List<HhVacancyDto> getFullVacancyInformation(List<HhVacancyDto> list) {
        List<HhVacancyDto> result = new ArrayList<>();
        for (HhVacancyDto element : list) {
            var vacancyDto = getVacancyById(element.getId());
            result.add(vacancyDto);
        }
        return result;
    }

    private OAuth2AccessToken getToken() {
        try {
            return authorizationService.getToken();
        } catch (IOException | ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private List<HhVacancyDto> getFullHhVacancy() {
        return getRecommendedVacancy();

    }

    private List<HhVacancyDto> getHhVacancy(String key) {
        return getPageRecommendedVacancy(key);
    }

    private List<HhVacancyDto> getRecommendedVacancy() {
        try {
            var list = service.getRecommendedVacancy(getToken(), getKey());
            var listEmployer = employerEntityService.extractEmployers(list);
            employerEntityService.saveAll(listEmployer);
            return list;
        } catch (IOException | ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private List<HhVacancyDto> getPageRecommendedVacancy(String key) {
        try {
            return service.getPageRecommendedVacancy(getToken(), 0, key).getItems();
        } catch (IOException | ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private String getKey() {
        return filterEntityService.getKey();
    }
}
