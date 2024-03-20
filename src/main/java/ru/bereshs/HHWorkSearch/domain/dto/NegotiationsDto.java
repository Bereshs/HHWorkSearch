package ru.bereshs.HHWorkSearch.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Отклики")
public class NegotiationsDto {
    @JsonProperty("message")
    private String message;
    @JsonProperty("resume_id")
    private String resumeId;
    @JsonProperty("vacancy_id")
    private String vacancyId;
}
