package ru.bereshs.HHWorkSearch.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
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
public class VacancyEntity implements FilteredVacancy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String hhId;
    private String url;
    private String name;
    private String experience;
    private String description;
    private LocalDateTime published;
    private int responses;
    private String employerId;
    private String employerName;
    @Enumerated(EnumType.STRING)
    private VacancyStatus status;

    public VacancyEntity(HhVacancyDto vacancyDto) {
        hhId = vacancyDto.getId();
        url = vacancyDto.getUrl();
        name = vacancyDto.getName();
        description = vacancyDto.getDescription();
        published = LocalDateTime.parse(dateWithoutTimeZone(vacancyDto.getPublishedAt()));
        responses = vacancyDto.getCounters().getTotalResponses();
        employerId = vacancyDto.getEmployer().getId();
        employerName = vacancyDto.getEmployer().getName();
        status=VacancyStatus.found;
        experience= vacancyDto.getExperience();
    }

    private String dateWithoutTimeZone(String date) {
        return date.substring(0, date.length() - 5);
    }


}
