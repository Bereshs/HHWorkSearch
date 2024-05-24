package ru.bereshs.hhworksearch.domain.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.bereshs.hhworksearch.domain.SettingsEntity;

@Getter
@Setter
@NoArgsConstructor
public class SettingsDto {
    private String name;
    private String value;

    public SettingsDto(SettingsEntity settings) {
        name = settings.getName();
        value = settings.getValue();
    }
}
