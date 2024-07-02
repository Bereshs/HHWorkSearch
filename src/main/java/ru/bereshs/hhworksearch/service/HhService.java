package ru.bereshs.hhworksearch.service;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.bereshs.hhworksearch.aop.Loggable;
import ru.bereshs.hhworksearch.config.AppConfig;

import ru.bereshs.hhworksearch.domain.ResumeEntity;
import ru.bereshs.hhworksearch.hhapiclient.HeadHunterClient;
import ru.bereshs.hhworksearch.hhapiclient.impl.HeadHunterClientRestTemplate;
import ru.bereshs.hhworksearch.hhapiclient.dto.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;


@Service
public class HhService {
    private final HeadHunterClient headHunterClient;

    private final AppConfig appConfig;

    @Autowired
    public HhService(HeadHunterClientRestTemplate headHunterClient, AppConfig appConfig) {
        this.headHunterClient = headHunterClient;
        this.appConfig = appConfig;
    }


    @Loggable
    public List<HhVacancyDto> getRecommendedVacancy(OAuth2AccessToken token, String key) throws IOException, ExecutionException, InterruptedException {
        HhListDto<HhVacancyDto> tempList = getPageRecommendedVacancy(token, 0, key);
        List<HhVacancyDto> vacancyList = new ArrayList<>(tempList.getItems());
        for (int i = 1; i < tempList.getPages(); i++) {
            tempList = getPageRecommendedVacancy(token, i, key);
            vacancyList.addAll(tempList.getItems());
        }
        return vacancyList;
    }

    public HhVacancyDto getVacancyById(String vacancyId, OAuth2AccessToken token) throws IOException, ExecutionException, InterruptedException {
        String uri = appConfig.getVacancyConnectionString(vacancyId);
        return headHunterClient.executeObject(Verb.GET, uri, token, HhVacancyDto.class);
    }

    @Loggable
    public HhListDto<HhNegotiationsDto> getHhNegotiationsDtoList(OAuth2AccessToken token) throws IOException, ExecutionException, InterruptedException {
        String uri = appConfig.getNegotiationsConnectionString();
        return headHunterClient.getObjects(Verb.GET, uri, token, HhNegotiationsDto.class);

    }

    @Loggable
    public HhListDto<HhViewsResume> getHhViewsResumeDtoList(OAuth2AccessToken token, String resumeId) throws IOException, ExecutionException, InterruptedException {
        String uri = appConfig.getResumeViewsConnectionString(resumeId);
        return headHunterClient.getObjects(Verb.GET, uri, token, HhViewsResume.class);
    }

    @Loggable
    public HhListDto<HhVacancyDto> getPageRecommendedVacancy(OAuth2AccessToken token, int page, String key) throws IOException, ExecutionException, InterruptedException {
        String uri = appConfig.getVacancyConnectionString(page, key);
        return headHunterClient.getObjects(Verb.GET, uri, token, HhVacancyDto.class);
    }

    @Loggable
    public HhListDto<HhVacancyDto> getPageRecommendedVacancyForResume(OAuth2AccessToken token, ResumeEntity resume) throws IOException, ExecutionException, InterruptedException {
        String uri = appConfig.getVacancyLikeResumeConnectionString(resume, 0);
        return headHunterClient.getObjects(Verb.GET, uri, token, HhVacancyDto.class);
    }

    @Loggable
    public HhListDto<HhResumeDto> getActiveResumes(OAuth2AccessToken token) throws IOException, ExecutionException, InterruptedException {
        String uri = appConfig.getResumesConnectionString();
        return headHunterClient.getObjects(Verb.GET, uri, token, HhResumeDto.class);
    }
    
    public HhResumeDto getResumeById(String resumeId, OAuth2AccessToken token) throws IOException, ExecutionException, InterruptedException {
        String uri = appConfig.getResumeByIdConnectrinString(resumeId);
        return headHunterClient.executeObject(Verb.GET, uri, token, HhResumeDto.class);
    }

    @Loggable
    public <T> HhListDto<T> get(String connectionString, OAuth2AccessToken token, Class<T> type) throws IOException, ExecutionException, InterruptedException {
        return headHunterClient.getObjects(Verb.GET, connectionString, token, type);
    }


    @Loggable
    public Map<HhSimpleListDto, Double> getLoyalEmployer(OAuth2AccessToken token, String resumeId) throws IOException, ExecutionException, InterruptedException {
        String uri = appConfig.getResumeViewsConnectionString(resumeId);
        var viewResume = headHunterClient.getAllPagesObject(Verb.GET, uri, token, HhViewsResume.class);

        Map<HhSimpleListDto, Long> countEmployerViewedResume = getCountEmployer(viewResume.getItems());
        List<HhVacancyDto> tmpRequests = getHhNegotiationsDtoList(token).getItems().stream().map(HhNegotiationsDto::getVacancy).toList();
        Map<HhSimpleListDto, Long> countEmployerRequestedResume = getCountEmployer(tmpRequests);

        return getRatingEmployers(countEmployerRequestedResume, countEmployerViewedResume).entrySet().stream()
                .filter(a -> a.getValue() > 1)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }


    @Loggable
    public Response postNegotiation(OAuth2AccessToken token, HashMap<String, String> body) throws IOException, ExecutionException, InterruptedException {
        String uri = appConfig.getNegotiationPostConnetcionString();
        var result = headHunterClient.executeWithBody(Verb.POST, uri, token, body);
        return result;
    }

    @Loggable
    public void updateResume(OAuth2AccessToken token, String id) throws IOException, ExecutionException, InterruptedException {
        String uri = appConfig.getPostResume(id);
        var result = headHunterClient.execute(Verb.POST, uri, token);
    }


    private Map<HhSimpleListDto, Double> getRatingEmployers(Map<HhSimpleListDto, Long> map1, Map<HhSimpleListDto, Long> map2) {
        Map<HhSimpleListDto, Double> result = new HashMap<>();
        for (Map.Entry<HhSimpleListDto, Long> entry : map1.entrySet()) {
            long map2EmployerCount = map2.getOrDefault(entry.getKey(), 0L);
            result.put(entry.getKey(), (double) (map2EmployerCount) / entry.getValue());
        }
        return result;
    }

    private Map<HhSimpleListDto, Long> getCountEmployer(List<? extends HasEmployer> listEmployers) {
        return listEmployers.stream().collect(Collectors.groupingBy(HasEmployer::getEmployer, Collectors.counting()));
    }

    @Loggable
    public String getResumeAccessType(String resumeId, OAuth2AccessToken token) throws IOException, ExecutionException, InterruptedException {
        String uri = appConfig.getResumeAccessTypesConnectionString(resumeId);
        var list = headHunterClient.getObjects(Verb.GET, uri, token, HhSimpleListDto.class).getItems();
        HhSimpleListDto active = getActive(list);
        return active != null ? active.getId() : null;
    }

    @Loggable
    public HhSimpleListDto getActive(List<HhSimpleListDto> list) {
        for (HhSimpleListDto hhSimpleListDto : list) {
            if (hhSimpleListDto.isActive()) {
                return hhSimpleListDto;
            }
        }
        return null;
    }

}
