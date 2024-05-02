package ru.bereshs.HHWorkSearch.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.bereshs.HHWorkSearch.domain.VacancyEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VacancyEntityRepository extends JpaRepository<VacancyEntity, Integer> {
    Optional<VacancyEntity> getByHhId(String hhid);

    VacancyEntity findFirstBy(Sort sort);

    List<VacancyEntity> getVacancyEntitiesByTimeStampAfter(LocalDateTime date);
}
