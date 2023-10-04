package ru.bereshs.HHWorkSearch.hhApiClient.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.bereshs.HHWorkSearch.model.data.EmployerEntity;
import ru.bereshs.HHWorkSearch.model.data.VacancyEntity;

import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class HhVacancyDto {
    int id;
    @JsonProperty("name")
    String title;
    HhAreaDto area;
    HhCountersDto counters;
    HhEmployerDto employer;
    HhSalaryDto salary;
    @JsonProperty("published_at")
    String publishedAt;
    @JsonProperty("apply_alternate_url")
    String url;
    LocalDateTime createdAt;

}
