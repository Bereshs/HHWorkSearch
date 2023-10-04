package ru.bereshs.HHWorkSearch.hhApiClient;

import com.github.scribejava.apis.HHApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ru.bereshs.HHWorkSearch.config.AppConfig;
import ru.bereshs.HHWorkSearch.hhApiClient.dto.HhListDto;
import ru.bereshs.HHWorkSearch.model.data.KeyEntity;
import ru.bereshs.HHWorkSearch.model.data.ResumeEntity;
import ru.bereshs.HHWorkSearch.model.data.VacancyEntity;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

@Service
public class HeadHunterClient {
    private final AppConfig config;
    private final Logger logger = Logger.getLogger(HeadHunterClient.class.getName());
    @Getter
    private final OAuth20Service authService;

    @Autowired
    public HeadHunterClient(AppConfig config) {
        authService = new ServiceBuilder(config.getHhClientId())
                .apiSecret(config.getHhClientSecret())
                .callback(config.getHhApiCallback())
                .build(HHApi.instance());

        this.config = config;
    }

    public OAuth2AccessToken requestAccessToken(String code) throws IOException, ExecutionException, InterruptedException {
        logger.info("Request access token code="+code);
        return authService.getAccessToken(code);
    }

    public OAuth2AccessToken requestRefreshToken(String refreshToken) throws IOException, ExecutionException, InterruptedException {
        logger.info("Request access token refresh="+refreshToken);
        return authService.refreshAccessToken(refreshToken);
    }

    public String getConnectionString() {
        return authService.getAuthorizationUrl();
    }



    public <T> T get(String url, Class<T> type) {
        MultiValueMap<String, String> params =  new LinkedMultiValueMap<>();
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(params, getHeaders());
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<T> response = restTemplate.getForEntity(url, type);
            logger.info("received data: " + response.getBody());
            logger.info("received headers: " + response.getHeaders());
            return response.getBody();
        } catch (HttpClientErrorException m) {
            logger.info("Request:" + request + " Received error: " + m.getMessage());
            return null;
        }
    }

    public <T> T post(String url, MultiValueMap<String, String> params, Class<T> type) {
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(params, getHeaders());
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<T> response = restTemplate.postForEntity(url, request, type);
            logger.info("received data: " + response.getBody());
            logger.info("received headers: " + response.getHeaders());
            return response.getBody();
        } catch (HttpClientErrorException m) {
            logger.info("Request:" + request + " Received error: " + m.getLocalizedMessage());
            return null;
        }
    }

    public HttpHeaders postGetCookies (String url, MultiValueMap<String, String> params) {
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(params, getHeaders());
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            logger.info("received data: " + response.getBody());
            logger.info("received headers: " + response.getHeaders());
            return response.getHeaders();
        } catch (HttpClientErrorException m) {
            logger.info("Request:" + request + " Received error: " + m.getLocalizedMessage());
            return null;
        }


    }


    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/x-www-form-urlencoded"));
        headers.set("User-Agent", config.getHhUserAgent());
        logger.info("created headers="+headers);
        return headers;
    }

}
