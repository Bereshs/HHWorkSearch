package ru.bereshs.HHWorkSearch.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.bereshs.HHWorkSearch.hhApiClient.HeadHunterClient;
import ru.bereshs.HHWorkSearch.model.data.KeyEntity;
import ru.bereshs.HHWorkSearch.model.storage.KeysEntityRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

@Service
public class AuthorizationService {

    private final KeysEntityRepository keysEntityRepository;

    private final HeadHunterClient client;

    private final Logger logger = Logger.getLogger(AuthorizationService.class.getName());

    private OAuth2AccessToken token;

    @Autowired
    public AuthorizationService(KeysEntityRepository keysEntityRepository, HeadHunterClient client) {
        this.keysEntityRepository = keysEntityRepository;
        this.client = client;
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

    public OAuth2AccessToken getToken(KeyEntity key) throws IOException, ExecutionException, InterruptedException {
        if (key.isExpires()) {
            token = client.requestRefreshToken(key.getRefreshToken());
            return token;
        }
        token = new OAuth2AccessToken(key.getAccessToken(), key.getTokenType(), key.getExpiresIn(), key.getRefreshToken(), key.getScope(), key.getRowResponse());
        return token;
    }

    public Response execute(Verb verb, String uri) throws IOException, ExecutionException, InterruptedException {
        OAuthRequest request = new OAuthRequest(verb, uri);
        client.getAuthService().signRequest(token, request);
        return client.getAuthService().execute(request);
    }

    public HashMap<String, ?> getMapBody(String body) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(body, HashMap.class);
    }

    public String getConnectionString() {
        return client.getConnectionString();
    }

}
