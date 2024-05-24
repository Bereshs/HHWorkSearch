package ru.bereshs.hhworksearch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bereshs.hhworksearch.domain.KeyEntity;

import java.util.Optional;

public interface KeyEntityRepository extends JpaRepository<KeyEntity, Integer> {
    Optional<KeyEntity> getByClientId(String clientId);

}
