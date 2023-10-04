package ru.bereshs.HHWorkSearch.model.data;

import com.github.scribejava.core.model.OAuth2AccessToken;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.logging.Logger;

@Entity
@Table(name = "keys")
@Data
public class KeyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private LocalDateTime time;
    private String authorizationCode;
    private String accessToken;
    private String refreshToken;
    private Integer expiresIn;
    private String tokenType;
    private String scope;
    private String clientId;
    private String rowResponse;

    public boolean isExpires() {
        LocalDateTime expireTime = time.plusSeconds(expiresIn);
        Logger.getLogger(this.getClass().getName()).info("expire "+LocalDateTime.now().isAfter(expireTime));
        return LocalDateTime.now().isAfter(expireTime);
    }

    public void set(OAuth2AccessToken token) {
        setAccessToken(token.getAccessToken());
        setRefreshToken(token.getRefreshToken());
        setExpiresIn(token.getExpiresIn());
        setTokenType(token.getTokenType());
        setScope(token.getScope());
        setRowResponse(token.getRawResponse());
    }
}
