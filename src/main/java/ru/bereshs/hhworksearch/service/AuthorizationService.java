package ru.bereshs.hhworksearch.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Verb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.bereshs.hhworksearch.repository.KeyEntityRepository;
import ru.bereshs.hhworksearch.config.AppConfig;
import ru.bereshs.hhworksearch.hhapiclient.HeadHunterClient;
import ru.bereshs.hhworksearch.domain.KeyEntity;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

@Service
public class AuthorizationService {
    private final KeyEntityRepository keysEntityRepository;

    private final HeadHunterClient client;
    private final AppConfig config;

    @Autowired
    public AuthorizationService(KeyEntityRepository keysEntityRepository, HeadHunterClient client, AppConfig config) {
        this.keysEntityRepository = keysEntityRepository;
        this.client = client;
        this.config = config;
    }


    public void authorization(String code) throws IOException, ExecutionException, InterruptedException {
        OAuth2AccessToken token = client.requestAccessToken(code);
        OAuthRequest request = new OAuthRequest(Verb.GET, "https://api.hh.ru/me");
        client.getAuthService().signRequest(token, request);
        client.getAuthService().execute(request);

        KeyEntity key = getByClientId(config.getHhClientId());
        key.set(token);
        key.setAuthorizationCode(code);
        key.setClientId(config.getHhClientId());
        key.set(token);
        keysEntityRepository.save(key);
    }

    public KeyEntity getByClientId(String clientId) {
        KeyEntity key = keysEntityRepository.getByClientId(clientId).orElse(null);
        if (key == null) {
            key = new KeyEntity();
            key.setClientId(clientId);
            keysEntityRepository.save(key);
        }
        return key;
    }

    public OAuth2AccessToken getToken() throws IOException, ExecutionException, InterruptedException {
        KeyEntity key = getByClientId(config.getHhClientId());

        if (!key.isValid() && key.getTime() == null) {
            save(key);
        }
        OAuth2AccessToken token;
        if (!key.isValid() && key.getRefreshToken() != null) {
            token = client.requestRefreshToken(key.getRefreshToken());
            return token;
        }
        if (!key.isValid()) {
            token = client.requestAccessToken(config.getHhClientId());
            return token;
        }
        token = new OAuth2AccessToken(key.getAccessToken(), key.getTokenType(), key.getExpiresIn(), key.getRefreshToken(), key.getScope(), key.getRowResponse());
        key.set(token);
        save(key);
        return token;
    }

    public HashMap<String, ?> getMapBody(String body) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(body, HashMap.class);
    }

    public String getConnectionString() {
        return client.getConnectionString();
    }

    public void save(KeyEntity key) {
        key.setTime(LocalDateTime.now());
        keysEntityRepository.save(key);
    }
}
