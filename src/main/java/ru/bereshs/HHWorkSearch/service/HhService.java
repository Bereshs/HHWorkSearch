package ru.bereshs.HHWorkSearch.service;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.Verb;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.startup.CredentialHandlerRuleSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.bereshs.HHWorkSearch.config.AppConfig;

import ru.bereshs.HHWorkSearch.hhApiClient.HeadHunterClient;
import ru.bereshs.HHWorkSearch.hhApiClient.dto.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;


@Service
@Slf4j
public class HhService {
    private final HeadHunterClient headHunterClient;

    private final AppConfig appConfig;

    @Autowired
    public HhService(HeadHunterClient headHunterClient, AppConfig appConfig) {
        this.headHunterClient = headHunterClient;
        this.appConfig = appConfig;
    }

    public List<HhVacancyDto> getRecommendedVacancy(OAuth2AccessToken token) throws IOException, ExecutionException, InterruptedException {
        HhListDto<HhVacancyDto> tempList = getPageRecommendedVacancy(token, 0);
        List<HhVacancyDto> vacancyList = new ArrayList<>(tempList.getItems());
        log.info("Received data found: " + tempList.getFound() + " pages: " + tempList.getPages() + " page: " + tempList.getPage() + " perPage: " + tempList.getPerPage());
        for (int i = 1; i < tempList.getPages(); i++) {
            tempList = getPageRecommendedVacancy(token, i);
            vacancyList.addAll(tempList.getItems());
        }
        return vacancyList;
    }


    public HhListDto<HhNegotiationsDto> getHhNegotiationsDtoList(OAuth2AccessToken token) throws IOException, ExecutionException, InterruptedException {
        String uri = appConfig.getNegotiationsConnectionString(0);
        return headHunterClient.getObjects(Verb.GET, uri, token, HhNegotiationsDto.class);

    }

    public HhListDto<HhViewsResume> getHhViewsResumeDtoList(OAuth2AccessToken token, String resumeId) throws IOException, ExecutionException, InterruptedException {
        String uri = appConfig.getResumeViewsConnectionString(resumeId);
        return headHunterClient.getObjects(Verb.GET, uri, token, HhViewsResume.class);
    }

    public HhListDto<HhVacancyDto> getPageRecommendedVacancy(OAuth2AccessToken token, int page) throws IOException, ExecutionException, InterruptedException {
        String uri = appConfig.getVacancyConnectionString(page);
        return headHunterClient.getObjects(Verb.GET, uri, token, HhVacancyDto.class);
    }


    public HhListDto<HhResumeDto> getActiveResumes(OAuth2AccessToken token) throws IOException, ExecutionException, InterruptedException {
        String uri = appConfig.getResumesConnectionString();
        return headHunterClient.getObjects(Verb.GET, uri, token, HhResumeDto.class);

    }

    public Map<HhEmployerDto, Double> getLoyalEmployer(OAuth2AccessToken token, String resumeId) throws IOException, ExecutionException, InterruptedException {
        String uri = appConfig.getResumeViewsConnectionString(resumeId);
        var viewResume = headHunterClient.getAllPagesObject(Verb.GET, uri, token, HhViewsResume.class);

        Map<HhEmployerDto, Long> countEmployerViewedResume = getCountEmployer(viewResume.getItems());
        List<HhVacancyDto> tmpRequests = getHhNegotiationsDtoList(token).getItems().stream().map(HhNegotiationsDto::getVacancy).toList();
        Map<HhEmployerDto, Long> countEmployerRequestedResume = getCountEmployer(tmpRequests);

        return getRatingEmployers(countEmployerRequestedResume, countEmployerViewedResume).entrySet().stream()
                .filter(a -> a.getValue() > 1)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Map<HhEmployerDto, Double> getRatingEmployers(Map<HhEmployerDto, Long> map1, Map<HhEmployerDto, Long> map2) {
        Map<HhEmployerDto, Double> result = new HashMap<>();
        for (Map.Entry<HhEmployerDto, Long> entry : map1.entrySet()) {
            long map2EmployerCount = map2.getOrDefault(entry.getKey(), 0L);
            result.put(entry.getKey(), (double) (map2EmployerCount) / entry.getValue());
        }
        return result;
    }

    private Map<HhEmployerDto, Long> getCountEmployer(List<? extends HasEmployer> listEmployers) {
        return listEmployers.stream().collect(Collectors.groupingBy(HasEmployer::getEmployer, Collectors.counting()));
    }

}
