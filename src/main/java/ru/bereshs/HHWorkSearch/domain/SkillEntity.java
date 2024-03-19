package ru.bereshs.HHWorkSearch.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "skills")
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor

public class SkillEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;
    private String description;

    public SkillEntity(String name) {
        this.name = name;
    }
    public String toString() {
        return "name: " + name
                + " description: " + description;
    }

}
