package ru.bereshs.HHWorkSearch.hhApiClient;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import ru.bereshs.HHWorkSearch.config.AppConfig;
import ru.bereshs.HHWorkSearch.hhApiClient.dto.HhListDto;
import ru.bereshs.HHWorkSearch.model.data.ResumeEntity;
import ru.bereshs.HHWorkSearch.model.data.VacancyEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Logger;

@Service
public class HeadHunterClient {
    private final AppConfig config;
    private final Logger logger = Logger.getLogger(HeadHunterClient.class.getName());

    @Setter
    private String token;

    @Autowired
    public HeadHunterClient(AppConfig config) {
        this.config = config;
    }


    public <T> T get(String url, Class<T> type) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(getHeaders()), type);
        if (response.getStatusCode().is4xxClientError()) {
            logger.info("Received error: " + response.getBody());
            return null;
        }
        return response.getBody();

    }

    public <T> T post(String url, MultiValueMap<String, String> params, Class<T> type) {
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(params, getHeaders());
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<T> response = restTemplate.postForEntity(url, request, type);
        if (response.getStatusCode().is4xxClientError()) {
            logger.info("Received error: " + response.getBody());
            return null;
        }
        return response.getBody();
    }

    public List<ResumeEntity> getUserResumeEntityList() {
        HhListDto<LinkedHashMap<String, String>> list = get("http://localhost:8020" + config.getHhResume(), HhListDto.class);
        List<ResumeEntity> resumeEntities = new ArrayList<>();
        list.getItems().forEach((item) -> {
            resumeEntities.add(createResumeEntity(item));
        });
        return resumeEntities;
    }

    public List<VacancyEntity> getVacancyEntityList() {
        HhListDto<LinkedHashMap<String, ?>> list = get("http://localhost:8020" + config.getHhVacancy(), HhListDto.class);
        List<VacancyEntity> resumeEntities = new ArrayList<>();
        list.getItems().forEach((item) -> {
            resumeEntities.add(createVacancyEntity(item));
        });
        return resumeEntities;
    }

    public ResumeEntity createResumeEntity(LinkedHashMap<String, String> item) {
        ResumeEntity resume = new ResumeEntity();
        resume.setUrl(item.get("alternate_url"));
        resume.setTitle(item.get("title"));
        resume.setHhId(item.get("id"));
        resume.setCreatedAt(getLocalDateTimeFromString(item.get("created")));
        return resume;
    }
    public VacancyEntity createVacancyEntity(LinkedHashMap<String, ?> item) {
        VacancyEntity vacancy = new VacancyEntity();
        vacancy.setUrl(item.get("url").toString());
        vacancy.setTitle(item.get("name").toString());
        vacancy.setHhId(item.get("id").toString());
        vacancy.setCreatedAt(getLocalDateTimeFromString(item.get("published_at").toString()));
        LinkedHashMap<String, String> employer = (LinkedHashMap<String, String>) item.get("employer");
        vacancy.setEmployerHhId(employer.get("id"));
        return vacancy;
    }

    public LocalDateTime getLocalDateTimeFromString(String dateString) {
        String date = dateString.substring(0, dateString.indexOf("+"));
        return LocalDateTime.parse(date);

    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/x-www-form-urlencoded"));
        headers.set("User-Agent", config.getHhUserAgent());
        if (token != null) {
            headers.setBearerAuth(token);
        }
        return headers;
    }

}
