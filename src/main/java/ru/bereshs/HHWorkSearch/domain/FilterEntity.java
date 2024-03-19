package ru.bereshs.HHWorkSearch.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Table(name = "filter")
@Entity
@Getter
@Setter
@Schema(description = "Фильтр")

public class FilterEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long id;
    String scope;
    String word;

    public String toString() {
        return "FilterEntity{scope=" + scope + ", word=" + word + "}";
    }
}
