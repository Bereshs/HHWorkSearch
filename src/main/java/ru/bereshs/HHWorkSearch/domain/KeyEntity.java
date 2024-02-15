package ru.bereshs.HHWorkSearch.domain;

import com.github.scribejava.core.model.OAuth2AccessToken;
import jakarta.persistence.*;
import lombok.Data;
import ru.bereshs.HHWorkSearch.exception.HhWorkSearchException;

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

    public boolean isValid() throws HhWorkSearchException {
        if (expiresIn == null || authorizationCode == null) {
            throw new HhWorkSearchException("Wrong expiresIn or authorizationCode parameter please vizit homepage for update this data");
        }
        LocalDateTime expireTime = time.plusSeconds(expiresIn);
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