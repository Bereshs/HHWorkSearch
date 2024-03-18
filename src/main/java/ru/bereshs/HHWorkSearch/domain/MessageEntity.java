package ru.bereshs.HHWorkSearch.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;
import ru.bereshs.HHWorkSearch.Repository.SkillsEntityRepository;

import java.util.List;
import java.util.logging.Logger;

@Entity
@Getter
@Setter
@Table(name = "message")
@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(length = 2048)
    private String header;
    @Column(length = 2048)
    private String footer;

    public String getMessage(List<SkillEntity> skills, String vacancyName) {
        StringBuilder competitions = new StringBuilder();
        for (SkillEntity element : skills) {
            if (element.getDescription() != null) {
                competitions.append(element.getDescription()).append(" ");
            }
        }
        return header.replaceAll("<VacancyName>", vacancyName) + competitions + footer;
    }
}
