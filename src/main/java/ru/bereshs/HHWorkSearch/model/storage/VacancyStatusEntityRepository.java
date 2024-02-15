package ru.bereshs.HHWorkSearch.model.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bereshs.HHWorkSearch.model.data.VacancyStatusEntity;

public interface VacancyStatusEntityRepository extends JpaRepository<VacancyStatusEntity, Integer> {
    public VacancyStatusEntity getByHhVacancyId(String hhVacancyId);
}
