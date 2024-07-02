package ru.bereshs.hhworksearch.service;

import com.github.scribejava.core.model.OAuth2AccessToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.bereshs.hhworksearch.aop.Loggable;
import ru.bereshs.hhworksearch.domain.ResumeEntity;
import ru.bereshs.hhworksearch.domain.SkillEntity;
import ru.bereshs.hhworksearch.domain.VacancyEntity;
import ru.bereshs.hhworksearch.domain.VacancyStatus;
import ru.bereshs.hhworksearch.hhapiclient.HhLocalDateTime;
import ru.bereshs.hhworksearch.hhapiclient.dto.HhVacancyDto;
import ru.bereshs.hhworksearch.producer.KafkaProducer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;


@Service
@RequiredArgsConstructor
public class SchedulerService {

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

    @Loggable
    public void  dailyLightTaskRequest() throws InterruptedException, IOException, ExecutionException {
        if (settingsService.isDemonActive()) {
            List<HhVacancyDto> vacancyList = getHhVacancy(getKey());
            postNegotiationWithRelevantVacancies(vacancyList);
            updateResume();
        }
    }

    @Loggable
    public void  dailyFullRequest() throws InterruptedException, IOException, ExecutionException {
        if (settingsService.isDemonActive()) {
            List<HhVacancyDto> vacancyList = getFullHhVacancy();
            postNegotiationWithRelevantVacancies(vacancyList);
        }
        updateVacancyStatus();
        sendMessageDailyReport();
    }

    @Loggable
    public void  dailyRecommendedRequest() throws InterruptedException, IOException, ExecutionException {
        if (settingsService.isDemonActive()) {
            List<HhVacancyDto> vacancyList = service.getPageRecommendedVacancyForResume(getToken(), resumeEntityService.getDefault()).getItems();
            postNegotiationWithRelevantVacancies(vacancyList);
        }
    }



    public void updateVacancyStatus() throws InterruptedException, IOException, ExecutionException {
            var negotiationsList = service.getHhNegotiationsDtoList(getToken());
            List<VacancyEntity> vacancyList = negotiationsList.getItems().stream().map(entity -> {
                VacancyEntity vacancy = new VacancyEntity(entity.getVacancy());
                vacancy.setStatus(entity.getState().getId());
                return vacancy;
            }).toList();
            vacancyEntityService.updateVacancyStatusFromList(vacancyList);
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
            if (!vacancyEntity.getStatus().equals(VacancyStatus.request)
                    && !vacancyEntity.getStatus().equals(VacancyStatus.REQUEST)) {
                ResumeEntity resume = resumeEntityService.getRelevantResume(vacancy);
                List<SkillEntity> skills = skillsEntityService.extractVacancySkills(vacancy);
                negotiationsService.doNegotiationWithRelevantVacancy(vacancy, resume.getHhId(), skills);
            }
        }
    }

    @Loggable
    private List<HhVacancyDto> getRelevantVacancies(List<HhVacancyDto> vacancyList) throws InterruptedException {
        List<HhVacancyDto> filtered = filterEntityService.doFilterNameAndExperience(vacancyList);
        List<HhVacancyDto> list = vacancyEntityService.getUnique(filtered);
        var full = getFullVacancyInformation(list);
        vacancyEntityService.updateTimeStamp(full);
        filtered = filterEntityService.doFilterDescription(full);
        return filtered;
    }


    public HhVacancyDto getVacancyById(String id) throws InterruptedException {
        try {
            return service.getVacancyById(id, getToken());
        } catch (IOException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Loggable
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

    @Loggable
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
