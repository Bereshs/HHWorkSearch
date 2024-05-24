package ru.bereshs.hhworksearch.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bereshs.hhworksearch.hhApiClient.dto.HhSimpleListDto;

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
