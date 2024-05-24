package ru.bereshs.hhworksearch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bereshs.hhworksearch.domain.Employer;

public interface EmployerEntityRepository extends JpaRepository<Employer, Integer> {
    Employer getByHhId(String hhId);

    boolean existsByHhId(String hhId);

}
