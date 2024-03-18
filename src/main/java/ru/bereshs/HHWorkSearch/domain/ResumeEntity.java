package ru.bereshs.HHWorkSearch.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bereshs.HHWorkSearch.hhApiClient.dto.HhResumeDto;

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

    public ResumeEntity(HhResumeDto resumeDto) {
        setHhId(resumeDto.getId());
        setTitle(resumeDto.getTitle());
        setUrl(resumeDto.getUrl());
    }
}
