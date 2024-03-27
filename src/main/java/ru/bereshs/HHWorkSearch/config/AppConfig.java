package ru.bereshs.HHWorkSearch.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.bereshs.HHWorkSearch.domain.ResumeEntity;

import static java.util.Objects.isNull;

@Data
@Component
@ConfigurationProperties(prefix = "app")
public class AppConfig {

    String hhApiUri;
    String hhUserAgent;
    String hhClientId;
    String hhClientSecret;
    String emailEmail;
    String emailPassword;
    String hhVacancy;
    String hhResume;
    String hhApiCallback;
    String hhApiTokenUri;

    public String getVacancyConnectionString(Integer page) {
        String uri = "https://api.hh.ru/vacancies?responses_count_enabled=true" +
                "&period=1" +
                "&order_by=publication_time" +
                "&vacancy_search_fields=name" +
                "&text=java" +
                "&per_page=100";
        if (!isNull(page) && page > 0) {
            uri += "&page=" + page;
        }
        return uri;
    }

    public String getVacancyLikeResumeConnectionString(ResumeEntity resume, int page) {
        String uri = "https://api.hh.ru/resumes/" + resume.getHhId() + "/similar_vacancies" +
                "?period=1" +
                "&per_page=100";
        if (!isNull(page) && page > 0) {
            uri += "&page=" + page;
        }
        return uri;
    }

    public String getNegotiationsConnectionString(Integer page) {
        String uri = "https://api.hh.ru/negotiations?" +
                "order_by=updated_at" +
                "&per_page=100";

        return uri;
    }

    public String getResumeViewsConnectionString(String resumeId) {
        String uri = "https://api.hh.ru/resumes/" + resumeId + "/views";
        return uri;
    }


    public String getResumesConnectionString() {
        String uri = "https://api.hh.ru/resumes/mine";
        return uri;
    }

    public String getResumeByIdConnectrinString(String resumeId) {
        String uri = "https://api.hh.ru/resumes/" + resumeId;
        return uri;
    }

    public String getVacancyConnectionString(String id) {
        String uri = "https://api.hh.ru/vacancies/" + id;
        return uri;
    }

    public String getNegotiationPostConnetcionString() {
        String uri = "https://api.hh.ru/negotiations";
        return uri;
    }

    public String getResumeAccessTypesConnectionString(String resumeId) {
        String uri = "https://api.hh.ru/resumes/" + resumeId + "/access_types";
        return uri;
    }
}
