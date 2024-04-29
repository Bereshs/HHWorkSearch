package ru.bereshs.HHWorkSearch.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bereshs.HHWorkSearch.hhApiClient.dto.HhListDto;
import ru.bereshs.HHWorkSearch.hhApiClient.dto.HhSimpleListDto;

@Entity
@Data
@Table(name = "employer")
@NoArgsConstructor
public class Employer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String hhId;
    @JsonProperty("name")
    private String name;
    @JsonProperty("url")
    private String url;
    @JsonProperty("alternate_url")
    private String alternateUrl;

    public Employer(HhSimpleListDto employerDto) {
        setHhId(employerDto.getId());
        setName(employerDto.getName());
        setUrl(employerDto.getUrl());
        setAlternateUrl(employerDto.getAlternateUrl());
    }

}
