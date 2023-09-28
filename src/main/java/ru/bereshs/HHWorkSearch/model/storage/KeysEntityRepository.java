package ru.bereshs.HHWorkSearch.model.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bereshs.HHWorkSearch.model.data.KeyEntity;

public interface KeysEntityRepository extends JpaRepository<KeyEntity, Integer> {
    KeyEntity getByClientId(String clientId);

}
