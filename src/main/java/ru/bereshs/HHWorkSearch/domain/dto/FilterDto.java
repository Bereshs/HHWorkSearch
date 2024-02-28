package ru.bereshs.HHWorkSearch.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FilterDto {
    private String scope;
    private List<String> words;
}
