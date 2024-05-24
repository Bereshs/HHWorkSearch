package ru.bereshs.hhworksearch.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "Фильтр")
public class FilterDto {
    private String scope;
    private List<String> words;
}
