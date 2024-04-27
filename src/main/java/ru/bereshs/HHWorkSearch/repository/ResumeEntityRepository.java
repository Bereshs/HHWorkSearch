package ru.bereshs.HHWorkSearch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bereshs.HHWorkSearch.domain.ResumeEntity;

import java.util.Optional;

@Repository
public interface ResumeEntityRepository extends JpaRepository<ResumeEntity, Integer> {
    Optional<ResumeEntity> getResumeEntityByHhId(String hhId);
    Optional<ResumeEntity> getById(long id);

}
