package ru.bereshs.HHWorkSearch.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NegotiationsDto {
    @JsonProperty("message")
    private String message;
    @JsonProperty("resume_id")
    private String resumeId;
    @JsonProperty("vacancy_id")
    private String vacancyId;
}
