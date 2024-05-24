package ru.bereshs.hhworksearch.hhApiClient.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ru.bereshs.hhworksearch.domain.FilteredVacancy;
import ru.bereshs.hhworksearch.hhApiClient.HhLocalDateTime;

import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)

@Slf4j
public class HhVacancyDto implements HasEmployer, FilteredVacancy {
    String id;
    String name;
    HhSimpleListDto area;
    HhCountersDto counters;
    HhSimpleListDto employer;
    HhSalaryDto salary;
    HhSimpleListDto experience;
    @JsonProperty("published_at")
    String publishedAt;
    @JsonProperty("apply_alternate_url")
    String url;
    LocalDateTime createdAt;
    String description;
    @JsonProperty("alternate_url")
    String alternateUrl;
    String urlRequest;
    @JsonProperty("key_skills")
    List<HhSimpleListDto> skills;

    public void convertDate() {
        setCreatedAt(HhLocalDateTime.decodeLocalData(getPublishedAt()));
    }

    public String getExperience() {
        if (experience==null) {
            experience = new HhSimpleListDto();
        }
        return experience.getId();
    }

    public void setDescription(String description) {
        if (description == null) {
            description = "";
        }

        this.description = description.toLowerCase().replaceAll("<[^>]*>", "").replaceAll("&quot;", "").replaceAll("&amp;", "").replaceAll("\\n", "");

    }

    public HhCountersDto getCounters() {
        if (counters == null) {
            return new HhCountersDto();
        }
        return counters;
    }

    @Override
    public List<String> getSkillStringList() {
        if (skills == null) {
            return null;
        }
        return skills.stream().map(HhSimpleListDto::getName).toList();
    }

    @Override
    public String toString() {
        return "name:" + name + " description:" + description;
    }
}
