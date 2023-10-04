package ru.bereshs.HHWorkSearch.hhApiClient.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class HhEmployerDto {
    private int id;
    private String name;
    @JsonProperty("alternate_url")
    private String alternateUrl;
    private String url;
    public String toString() {
        return getName();
    }
}
