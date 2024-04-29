package ru.bereshs.HHWorkSearch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bereshs.HHWorkSearch.domain.Employer;

public interface EmployerEntityRepository extends JpaRepository<Employer, Integer> {
    Employer getByHhId(String hhId);

    boolean existsByHhId(String hhId);

}
