package ru.bereshs.HHWorkSearch.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.bereshs.HHWorkSearch.domain.SettingsEntity;
import ru.bereshs.HHWorkSearch.domain.dto.SettingsDto;
import ru.bereshs.HHWorkSearch.service.SettingsService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class SettingsController {

    private final SettingsService service;

    @PostMapping("/api/settings")
    public String setParameter (@RequestBody SettingsDto settingsDto) {

        Optional<SettingsEntity> settingsEntity =service.getByName(settingsDto.getName());
        if(settingsEntity.isPresent()) {
            var entity = settingsEntity.get();
            entity.setValue(settingsDto.getValue());
            service.save(entity);
        } else {
            service.save(new SettingsEntity(settingsDto));
        }

        return "ok";
    }

    @GetMapping("/api/settings")
    public List<SettingsDto> getSettings () {

        return service.getAll().stream().map(SettingsDto::new).toList();
    }

    @GetMapping("/api/settings/demon")
    public boolean isDemonActive() {
        return service.isDemonActive();
    }
}
