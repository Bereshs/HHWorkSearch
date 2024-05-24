package ru.bereshs.hhworksearch.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.bereshs.hhworksearch.domain.SettingsEntity;
import ru.bereshs.hhworksearch.repository.SettingsRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SettingsService {

    private final SettingsRepository settingsRepository;

    public Optional<SettingsEntity> getByName(String name) {
        return settingsRepository.getByName(name);
    }

    public String getValueByName(String name) {
        var result = settingsRepository.getByName(name);
        if (result.isEmpty()) return "";
        return result.get().getValue();

    }

    public List<SettingsEntity> getAll() {
        return settingsRepository.findAll();
    }

    public void save(SettingsEntity entity) {
        settingsRepository.save(entity);
    }

    public boolean isDemonActive() {
        var result = settingsRepository.getByName("app.demon-active");
        return result.filter(entity -> Boolean.parseBoolean(entity.getValue())).isPresent();
    }

    public String getAppHHUserAgent() {
        return getValueByName("app.hh-user-agent");
    }

    public String getAppHHClientId() {
        return getValueByName("app.hh-client-id");
    }

    public String getAppHHClientSecret() {
        return getValueByName("app.hh-client-secret");
    }

    public String getAppTelegramToken() {
        return getValueByName("app.telegram.token");
    }

    public String getAppClientId() {
        return getValueByName("app.clientId");
    }

}
