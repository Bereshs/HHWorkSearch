package ru.bereshs.hhworksearch.config;

import com.github.scribejava.core.model.OAuth2AccessToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import ru.bereshs.hhworksearch.domain.*;
import ru.bereshs.hhworksearch.hhapiclient.HhLocalDateTime;
import ru.bereshs.hhworksearch.hhapiclient.dto.HhVacancyDto;
import ru.bereshs.hhworksearch.producer.KafkaProducer;
import ru.bereshs.hhworksearch.service.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
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
    private static final String inactiveDaemonMessage = "daemon is inactive";


    @Scheduled(cron = "0 0 9-18 * * *")
    public void scheduleDayLightTask() throws IOException, ExecutionException, InterruptedException {
        if (settingsService.isDemonActive()) {
            log.info("starting scheduled task");
            List<HhVacancyDto> vacancyList = getHhVacancy(getKey());
            postNegotiationWithRelevantVacancies(vacancyList);
            updateResume();
        } else {
            log.info(inactiveDaemonMessage);
        }
    }

    @Scheduled(cron = "0 30 19 * * *")
    public void scheduleDailyFullRequest() throws InterruptedException {
        if (settingsService.isDemonActive()) {
            log.info("request full hhVacancies");
            List<HhVacancyDto> vacancyList = getFullHhVacancy();
            postNegotiationWithRelevantVacancies(vacancyList);
        } else {
            log.info(inactiveDaemonMessage);
        }

        updateVacancyStatus();

        sendMessageDailyReport();
    }

    @Scheduled(cron = "0 30 18 * * *")
    public void scheduleDailyRecommendedRequest() throws IOException, ExecutionException, InterruptedException {
        if (settingsService.isDemonActive()) {
            log.info("request recommended hhVacancies");
            List<HhVacancyDto> vacancyList = service.getPageRecommendedVacancyForResume(getToken(), resumeEntityService.getDefault()).getItems();
            postNegotiationWithRelevantVacancies(vacancyList);
        } else {
            log.info(inactiveDaemonMessage);
        }
    }


    public void updateVacancyStatus() throws InterruptedException {
        try {
            var negotiationsList = service.getHhNegotiationsDtoList(getToken());
            List<VacancyEntity> vacancyList = negotiationsList.getItems().stream().map(entity -> {
                VacancyEntity vacancy = new VacancyEntity(entity.getVacancy());
                vacancy.setStatus(entity.getState().getId());
                return vacancy;
            }).toList();
            vacancyEntityService.updateVacancyStatusFromList(vacancyList);
        } catch (IOException | ExecutionException exception) {
            log.error(Arrays.toString(exception.getStackTrace()));
        }
    }

    private void sendMessageDailyReport() {
        String message = vacancyEntityService.getDaily();
        producer.produceDefault(message);

    }

    private void postNegotiationWithRelevantVacancies(List<HhVacancyDto> vacancyList) throws InterruptedException {
        if (vacancyList.isEmpty()) return;
        var filtered = getRelevantVacancies(vacancyList);
        vacancyEntityService.saveAll(filtered);
        postNegotiations(filtered);
        vacancyEntityService.changeAllStatus(filtered, VacancyStatus.REQUEST);
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

    private void postNegotiations(List<HhVacancyDto> filtered) throws InterruptedException {
        for (HhVacancyDto vacancy : filtered) {
            VacancyEntity vacancyEntity = vacancyEntityService.getByVacancyDto(vacancy);
            if (!vacancyEntity.getStatus().equals(VacancyStatus.REQUEST)) {
                ResumeEntity resume = resumeEntityService.getRelevantResume(vacancy);
                List<SkillEntity> skills = skillsEntityService.extractVacancySkills(vacancy);
                negotiationsService.doNegotiationWithRelevantVacancy(vacancy, resume.getHhId(), skills);
            }
        }
    }

    private List<HhVacancyDto> getRelevantVacancies(List<HhVacancyDto> vacancyList) throws InterruptedException {
        log.info("total received vacancy list size:  " + vacancyList.size());
        List<HhVacancyDto> filtered = filterEntityService.doFilterNameAndExperience(vacancyList);
        log.info("total size vacancy after name filter: " + filtered.size());
        List<HhVacancyDto> list = vacancyEntityService.getUnique(filtered);
        log.info("total size after unique: " + list.size());
        var full = getFullVacancyInformation(list);
        vacancyEntityService.updateTimeStamp(full);
        filtered = filterEntityService.doFilterDescription(full);
        log.info("total size after full filter: " + filtered.size());
        return filtered;
    }


    public HhVacancyDto getVacancyById(String id) throws InterruptedException {
        try {
            return service.getVacancyById(id, getToken());
        } catch (IOException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public List<HhVacancyDto> getFullVacancyInformation(List<HhVacancyDto> list) throws InterruptedException {
        List<HhVacancyDto> result = new ArrayList<>();
        for (HhVacancyDto element : list) {
            var vacancyDto = getVacancyById(element.getId());
            result.add(vacancyDto);
        }
        return result;
    }

    private OAuth2AccessToken getToken() throws InterruptedException {
        try {
            return authorizationService.getToken();
        } catch (IOException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private List<HhVacancyDto> getFullHhVacancy() throws InterruptedException {
        return getRecommendedVacancy();

    }

    private List<HhVacancyDto> getHhVacancy(String key) throws InterruptedException {
        return getPageRecommendedVacancy(key);
    }

    private List<HhVacancyDto> getRecommendedVacancy() throws InterruptedException {
        try {
            var list = service.getRecommendedVacancy(getToken(), getKey());
            var listEmployer = employerEntityService.extractEmployers(list);
            employerEntityService.saveAll(listEmployer);
            return list;
        } catch (IOException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private List<HhVacancyDto> getPageRecommendedVacancy(String key) throws InterruptedException {
        try {
            return service.getPageRecommendedVacancy(getToken(), 0, key).getItems();
        } catch (IOException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private String getKey() {
        return filterEntityService.getKey();
    }
}
