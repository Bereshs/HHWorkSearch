package ru.bereshs.HHWorkSearch.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bereshs.HHWorkSearch.domain.MessageEntity;

@Repository
public interface MessageEntityRepository extends JpaRepository<MessageEntity, Long> {
}
