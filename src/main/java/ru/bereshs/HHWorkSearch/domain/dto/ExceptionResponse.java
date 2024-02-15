package ru.bereshs.HHWorkSearch.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
public class ExceptionResponse {
    String message;
    LocalDateTime time;
}
