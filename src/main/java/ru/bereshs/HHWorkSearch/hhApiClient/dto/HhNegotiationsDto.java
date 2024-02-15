package ru.bereshs.HHWorkSearch.hhApiClient.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.bereshs.HHWorkSearch.domain.VacancyEntity;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class HhNegotiationsDto {
    @JsonProperty("created_at")
    private String createdAt;
    private String id;
    @JsonProperty("state")
    private HhNegotiationsStateDto state;
    @JsonProperty("updated_at")
    private String updatedAt;
    @JsonProperty("vacancy")
    private HhVacancyDto vacancy;
}
