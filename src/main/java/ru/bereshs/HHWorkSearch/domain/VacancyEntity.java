package ru.bereshs.HHWorkSearch.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.bereshs.HHWorkSearch.domain.Employer;
import ru.bereshs.HHWorkSearch.hhApiClient.dto.HhVacancyDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "vacancy")
@NoArgsConstructor
@Slf4j
@Schema(description = "Вакансия")

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
    private LocalDateTime timeStamp;

    public VacancyEntity(HhVacancyDto vacancyDto) {
        hhId = vacancyDto.getId();
        url = vacancyDto.getAlternateUrl();
        setName(vacancyDto.getName());
        setDescription(vacancyDto.getDescription());
        published = LocalDateTime.parse(dateWithoutTimeZone(vacancyDto.getPublishedAt()));
        responses = vacancyDto.getCounters() == null ? 0 : vacancyDto.getCounters().getTotalResponses();
        employerId = vacancyDto.getEmployer().getId();
        employerName = vacancyDto.getEmployer().getName();
        status = VacancyStatus.found;
        experience = vacancyDto.getExperience();
        timeStamp = LocalDateTime.now();
    }

    private String dateWithoutTimeZone(String date) {
        return date.substring(0, date.length() - 5);
    }


    @Override
    public List<String> getSkillStringList() {
        return null;
    }

    public boolean isFull() {
        return description != null;
    }

    public void setDescription(String description) {
        if (description == null) {
            return;
        }
        String simpleString = description.toLowerCase().replaceAll("<[^>]*>", "").replaceAll("&quot;", "").replaceAll("&amp;", "").replaceAll("\\n", "");
        this.description = simpleString.length() > 255 ? simpleString.substring(0, 255) : simpleString;
    }

    public void setName(String name) {
        if (name == null) {
            return;
        }
        this.name = name.toLowerCase();
    }
}
