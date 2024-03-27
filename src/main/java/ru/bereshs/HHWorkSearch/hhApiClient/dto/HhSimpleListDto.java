package ru.bereshs.HHWorkSearch.hhApiClient.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class HhSimpleListDto {
    private String id;
    private String name;
    @JsonProperty("alternate_url")
    private String alternateUrl;
    private String url;
    private boolean active;

    public String toString() {
        return "id:" + id + " name:" + name + " active:" + active;
    }
}
