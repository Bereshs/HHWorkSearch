package ru.bereshs.HHWorkSearch.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bereshs.HHWorkSearch.domain.RatingEmployer;

import java.util.Optional;

@Repository
public interface RatingEmployerRepository extends JpaRepository<RatingEmployer, Integer> {
    Optional<RatingEmployer> findByEmployerId(String employerId);

}
