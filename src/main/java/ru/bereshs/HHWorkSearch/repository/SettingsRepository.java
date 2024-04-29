package ru.bereshs.HHWorkSearch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bereshs.HHWorkSearch.domain.SettingsEntity;
import ru.bereshs.HHWorkSearch.domain.dto.SettingsDto;

import java.util.Optional;

@Repository
public interface SettingsRepository extends JpaRepository<SettingsEntity, Long> {
    Optional<SettingsEntity> getByName(String name);

}
