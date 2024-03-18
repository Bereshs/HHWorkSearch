package ru.bereshs.HHWorkSearch.hhApiClient.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
    @JsonProperty("key_skills")
    List<HhVacancySkillsDto> skills;

    public void convertDate() {
        setCreatedAt(HhLocalDateTime.decodeLocalData(getPublishedAt()));
    }

    public String getExperience() {
        return  experience.getId();
    }

    public void setDescription(String description) {
        if(description==null) {
            description="";
        }

        this.description = description.toLowerCase().replaceAll("<[^>]*>", "").replaceAll("&quot;", "").replaceAll("&amp;", "").replaceAll("\\n", "");

    }
    @Override
    public List<String> getSkillStringList() {
        if (skills==null) {
            return null;
        }
        return skills.stream().map(HhVacancySkillsDto::getName).toList();
    }

    @Override
    public String toString(){
        return "name:"+name+" description:"+description;
    }
}
