package ru.bereshs.HHWorkSearch.model.storage;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.bereshs.HHWorkSearch.model.data.VacancyEntity;

public interface VacancyEntityRepository extends JpaRepository<VacancyEntity, Integer> {
    VacancyEntity getByHhId(String hhid);
}
