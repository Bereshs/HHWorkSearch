package ru.bereshs.HHWorkSearch.model.dto;

import lombok.Data;

@Data
public class RequestAuthorizationCode {
    private String grantType = "authorization_code";
    private String clientId;
    private String clientSecret;
    private String code;
}
