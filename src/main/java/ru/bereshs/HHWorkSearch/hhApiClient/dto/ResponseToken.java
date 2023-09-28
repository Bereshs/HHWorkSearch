package ru.bereshs.HHWorkSearch.hhApiClient.dto;

import lombok.Data;

@Data
public class ResponseToken {
    private String accessToken;
    private String tokenType;
    private Long expiresIn;
    private String refreshToken;
}
