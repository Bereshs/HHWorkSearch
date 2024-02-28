package ru.bereshs.HHWorkSearch.hhApiClient.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ru.bereshs.HHWorkSearch.domain.FilteredVacancy;
import ru.bereshs.HHWorkSearch.hhApiClient.HhLocalDateTime;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)

@Slf4j
public class HhVacancyDto implements HasEmployer, FilteredVacancy {
    String id;
    String name;
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

    public String getExperience() {
        return experience.getId();
    }

}
