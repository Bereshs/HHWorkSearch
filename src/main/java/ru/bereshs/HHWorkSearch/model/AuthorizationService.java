package ru.bereshs.HHWorkSearch.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ru.bereshs.HHWorkSearch.config.AppConfig;
import ru.bereshs.HHWorkSearch.hhApiClient.HeadHunterClient;
import ru.bereshs.HHWorkSearch.hhApiClient.dto.ResponseToken;
import ru.bereshs.HHWorkSearch.model.data.KeyEntity;
import ru.bereshs.HHWorkSearch.model.storage.KeysEntityRepository;

import java.time.LocalDateTime;
import java.util.logging.Logger;

@Service
public class AuthorizationService {

    private final KeysEntityRepository keysEntityRepository;

    private final HeadHunterClient client;

    private final Logger logger = Logger.getLogger(AuthorizationService.class.getName());


    private final AppConfig config;

    @Autowired
    public AuthorizationService(KeysEntityRepository keysEntityRepository, HeadHunterClient client, AppConfig config) {
        this.keysEntityRepository = keysEntityRepository;
        this.client = client;
        this.config = config;
    }

    public void save(KeyEntity key) {
        updateTime(key);
    }

    public KeyEntity getByClientId(String clientId) {
        KeyEntity key = keysEntityRepository.getByClientId(clientId);
        if (key == null) {
            key = new KeyEntity();
            key.setClientId(clientId);
            save(key);
        }
        return key;
    }

    private void updateTime(KeyEntity key) {
        key.setTime(LocalDateTime.now());
        keysEntityRepository.save(key);
        logger.info("saving key " + key);
    }

    public KeyEntity getToken(KeyEntity key) {
        logger.info("request for get token " + key);
        if (!key.isExpired()) {
            return key;
        }

        if (key.getAuthorizationCode() != null && key.getRefreshToken() != null) {
            updateTokenRequest(key);
            return key;
        }

        if (key.getAuthorizationCode() != null) {
            return newTokenRequest(key);
        }

        return new KeyEntity();
    }


    private KeyEntity updateTokenRequest(KeyEntity key) {
        ResponseToken responseToken = client.post("http://localhost:8020",
                createParamsFromKey(key, "refresh_token"),
                ResponseToken.class);

        return saveKeyFromResponseToken(responseToken, key);
    }

    private KeyEntity newTokenRequest(KeyEntity key) {

        ResponseToken responseToken = client.post("http://localhost:8020",
                createParamsFromKey(key, "authorization_code"),
                ResponseToken.class);

        return saveKeyFromResponseToken(responseToken, key);
    }

    private KeyEntity saveKeyFromResponseToken(ResponseToken responseToken, KeyEntity key) {
        key.setAccessToken(responseToken.getAccessToken());
        key.setExpiresIn(responseToken.getExpiresIn());
        key.setRefreshToken(responseToken.getRefreshToken());
        save(key);

        return key;
    }

    private MultiValueMap<String, String> createParamsFromKey(KeyEntity key, String grantType) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", grantType);
        if (grantType.equals("refresh_token")) {
            params.add("refresh_token", key.getRefreshToken());
        }
        if (grantType.equals("authorization_code")) {
            params.add("client_id", config.getHhClientId());
            params.add("client_secret", config.getHhClientSecret());
            params.add("code", key.getAuthorizationCode());
        }

        return params;
    }

}
