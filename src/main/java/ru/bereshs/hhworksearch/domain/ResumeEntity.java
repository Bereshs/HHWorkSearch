package ru.bereshs.hhworksearch.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bereshs.hhworksearch.hhApiClient.HhLocalDateTime;
import ru.bereshs.hhworksearch.hhApiClient.dto.HhResumeDto;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "resume")
@NoArgsConstructor
public class ResumeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String hhId;
    private String title;
    private String url;
    private boolean isDefault;
    private LocalDateTime timeStamp;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String accessType;
    @Column(columnDefinition = "TEXT")
    private String description;
    private LocalDateTime nextPublish;

    public ResumeEntity(HhResumeDto resumeDto) {
        setHhId(resumeDto.getId());
        setTitle(resumeDto.getTitle());
        setUrl(resumeDto.getUrl());
        setCreatedAt(HhLocalDateTime.decodeLocalData(resumeDto.getCreatedAt()));
        setUpdatedAt(HhLocalDateTime.decodeLocalData(resumeDto.getUpdatedAt()));
        setTimeStamp(LocalDateTime.now());
    }

}
