package ru.bereshs.HHWorkSearch.domain.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.bereshs.HHWorkSearch.domain.SettingsEntity;

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
