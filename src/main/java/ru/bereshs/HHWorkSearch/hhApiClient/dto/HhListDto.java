package ru.bereshs.HHWorkSearch.hhApiClient.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class HhListDto<T> {
    @JsonIgnoreProperties
    private String arguments;
    @JsonIgnoreProperties
    private String clusters;
    @JsonIgnoreProperties
    private String fixes;
    private int found;
    private List<T> items;
    private int page;
    private int pages;
    @JsonIgnoreProperties
    @JsonProperty("per_page")
    private int perPage;
    @JsonIgnoreProperties
    private String suggests;
}
