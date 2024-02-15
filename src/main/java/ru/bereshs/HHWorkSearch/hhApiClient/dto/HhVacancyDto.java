package ru.bereshs.HHWorkSearch.hhApiClient.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ru.bereshs.HHWorkSearch.hhApiClient.HhLocalDateTime;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)

@Slf4j
public class HhVacancyDto implements HasEmployer {
    String id;
    @JsonProperty("name")
    String title;
    HhAreaDto area;
    HhCountersDto counters;
    HhEmployerDto employer;
    HhSalaryDto salary;
    HhExperienceDto experience;
    @JsonProperty("published_at")
    String publishedAt;
    @JsonProperty("apply_alternate_url")
    String url;
    LocalDateTime createdAt;
    String description;
    @JsonProperty("alternate_url")
    String alternateUrl;
    String urlRequest;

    public void convertDate() {
        setCreatedAt(HhLocalDateTime.decodeLocalData(getPublishedAt()));
    }

    public boolean isValid() {
        if (experience == null) {
            return true;
        }
        if (!experienceIsRelevant(experience.getId())) {
            return false;
        }
        return isContainsExcludeWords(title);
    }

    private boolean isContainsExcludeWords(String titleVacancy) {
        for (String excludeWord : getExcludeWordsList()) {
            if (titleVacancy.toLowerCase().contains(excludeWord.toLowerCase())) {
                return false;
            }
            if (getDescription() != null && description.toLowerCase().contains(excludeWord.toLowerCase())) {
                return false;
            }
        }
        return true;
    }

    private boolean experienceIsRelevant(String experience) {
        boolean relevant = false;
        for (String exp : getRelevantExperience()) {
            if (exp.equalsIgnoreCase(experience)) {
                relevant = true;
                break;
            }
        }
        return relevant;
    }

    private List<String> getRelevantExperience() {
        List<String> experience = new ArrayList<>();
        experience.add("noExperience");
        experience.add("between1And3");
        return experience;
    }

    private List<String> getExcludeWordsList() {
        ArrayList<String> excludeWords = new ArrayList<>();
        excludeWords.add("Kotlin");
        excludeWords.add("senior");
        excludeWords.add("android");
        excludeWords.add("qa");
        excludeWords.add("ведущий");
        excludeWords.add("старший");
        excludeWords.add("главный");
        excludeWords.add("scala");
        excludeWords.add("ios");
        excludeWords.add("ruby");
        excludeWords.add("lead");
        excludeWords.add("fullstack");
        excludeWords.add("full-stack");
        excludeWords.add("full stack");
        excludeWords.add("аналитик");
        excludeWords.add("C++");
        excludeWords.add("С++");
        excludeWords.add("C#");
        excludeWords.add("C#");
        excludeWords.add("тестиров");
        excludeWords.add("Преподаватель");
        excludeWords.add("Frontend");
        excludeWords.add("1C");
        excludeWords.add("1С");
        excludeWords.add("SQL");
        excludeWords.add("Битрикс");
        excludeWords.add("Golang");

        excludeWords.add("специалист");
        excludeWords.add("Data Engineer");
        excludeWords.add("архитектор");
        excludeWords.add("ReactJS");
        excludeWords.add("Рекрутер");
        excludeWords.add("DevOps");
        excludeWords.add("Технический писатель");
        excludeWords.add("Technical interviewer");
        excludeWords.add("Риск-технолог");
        excludeWords.add("Руководитель");
        excludeWords.add("owner");
        excludeWords.add("Analyst");
        excludeWords.add("Integration Engineer");

        excludeWords.add("Инженер");
        excludeWords.add("менеджер");
        excludeWords.add("лидер");
        excludeWords.add("Эксперт");
        excludeWords.add("начальник");
        excludeWords.add("php");
        excludeWords.add("js");
        excludeWords.add("architect");
        excludeWords.add("game");

        excludeWords.add(".NET");
        excludeWords.add("Учитель");
        excludeWords.add("React");
        excludeWords.add("Manager");
        excludeWords.add("Администратор");
        excludeWords.add("administrator");
        excludeWords.add("Спикер");
        excludeWords.add("Principal");

        excludeWords.add("sap");
        excludeWords.add("баз данных");
        excludeWords.add("head");
        excludeWords.add("мобильн");
        excludeWords.add("sales");

        excludeWords.add("rpa");
        excludeWords.add("chief");
        excludeWords.add("publisher");


        excludeWords.add("phyton");
        excludeWords.add("дизайнер");
        excludeWords.add("Python");
        excludeWords.add("GO ");
        excludeWords.add(" GO");

        return excludeWords;
    }


}
