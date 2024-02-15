package ru.bereshs.HHWorkSearch.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.bereshs.HHWorkSearch.domain.Employer;
import ru.bereshs.HHWorkSearch.hhApiClient.dto.HhVacancyDto;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "vacancy")
@NoArgsConstructor
@Slf4j
public class VacancyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String hhId;
    private String url;
    private String name;
    private String description;
    private LocalDateTime published;
    private int responses;
    private int employerId;
    private String employerName;

    public VacancyEntity(HhVacancyDto vacancyDto) {
        hhId = vacancyDto.getId();
        url = vacancyDto.getUrl();
        name = vacancyDto.getTitle();
        description = vacancyDto.getDescription();
        published = LocalDateTime.parse(dateWithoutTimeZone(vacancyDto.getPublishedAt()));
        responses = vacancyDto.getCounters().getTotalResponses();
        employerId = vacancyDto.getEmployer().getId();
        employerName = vacancyDto.getEmployer().getName();
    }

    private String dateWithoutTimeZone(String date) {
        return date.substring(0, date.length() - 5);
    }
}
