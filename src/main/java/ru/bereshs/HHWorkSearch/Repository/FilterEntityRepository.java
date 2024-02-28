package ru.bereshs.HHWorkSearch.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bereshs.HHWorkSearch.domain.FilterEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface FilterEntityRepository extends JpaRepository<FilterEntity, Integer> {
    List<FilterEntity> findFilterEntityByScope(String scope);
    Optional<FilterEntity> findFilterEntityByScopeAndWord(String scope, String word);
}
