package ru.bereshs.hhworksearch.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.bereshs.hhworksearch.domain.dto.SettingsDto;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class SettingsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    String name;
    String value;

    public SettingsEntity(SettingsDto settingsDto) {
        this.name = settingsDto.getName();
        this.value = settingsDto.getValue();
    }
}
