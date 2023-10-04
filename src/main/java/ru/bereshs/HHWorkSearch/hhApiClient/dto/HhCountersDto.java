package ru.bereshs.HHWorkSearch.hhApiClient.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class HhCountersDto {
    @JsonProperty("total_responses")
    int totalResponses;

    public String toString() {
        return String.valueOf(totalResponses);
    }

    public Integer value() {
        return totalResponses;
    }
 }
