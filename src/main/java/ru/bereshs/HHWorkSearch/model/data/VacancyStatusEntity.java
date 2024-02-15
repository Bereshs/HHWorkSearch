package ru.bereshs.HHWorkSearch.model.data;

import jakarta.persistence.*;
import lombok.Data;
import ru.bereshs.HHWorkSearch.domain.VacancyStatus;

import java.time.LocalDateTime;

@Entity
@Data
public class VacancyStatusEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String hhVacancyId;
    @Enumerated(EnumType.STRING)
    private VacancyStatus status;
    private LocalDateTime statusTime;
}
