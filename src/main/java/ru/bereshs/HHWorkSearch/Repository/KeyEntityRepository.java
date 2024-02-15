package ru.bereshs.HHWorkSearch.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bereshs.HHWorkSearch.domain.KeyEntity;

import java.util.Optional;

public interface KeyEntityRepository extends JpaRepository<KeyEntity, Integer> {
    Optional<KeyEntity> getByClientId(String clientId);

}
