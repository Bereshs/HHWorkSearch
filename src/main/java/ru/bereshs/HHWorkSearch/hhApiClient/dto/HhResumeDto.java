package ru.bereshs.HHWorkSearch.hhApiClient.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.bereshs.HHWorkSearch.hhApiClient.HhLocalDateTime;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class HhResumeDto {
    private String id;
    private String title;
    private String url;
    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("updated_at")
    private String updatedAt;
    @JsonProperty("last_name")
    private String lastName;
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("middle_name")
    private String middleName;
    @JsonProperty("skill_set")
    private List<String> skillSet;
    @JsonProperty("skills")
    private String skills;
    @JsonProperty("visible")
    private boolean visible;

}
