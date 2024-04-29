package ru.bereshs.HHWorkSearch.hhApiClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.apis.HHApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.httpclient.multipart.BodyPartPayload;
import com.github.scribejava.core.model.*;
import com.github.scribejava.core.oauth.OAuth20Service;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ru.bereshs.HHWorkSearch.config.AppConfig;
import ru.bereshs.HHWorkSearch.hhApiClient.dto.HhListDto;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

@Service
@Slf4j
public class HeadHunterClient {
    private final AppConfig config;
    private final Logger logger = Logger.getLogger(HeadHunterClient.class.getName());
    @Getter
    private final OAuth20Service authService;

    @Autowired
    public HeadHunterClient(AppConfig config) {

        this.config = config;

        if (config.getHhClientSecret().length() < 5) {
            this.authService = null;
        } else {
            authService = new ServiceBuilder(config.getHhClientId())
                    .apiSecret(config.getHhClientSecret())
                    .callback(config.getHhApiCallback())
                    .build(HHApi.instance());

        }
    }

    private ObjectMapper getMapper() {
        return new ObjectMapper();
    }

    public Response execute(Verb verb, String uri, OAuth2AccessToken token) throws IOException, ExecutionException, InterruptedException {
        OAuthRequest request = new OAuthRequest(verb, uri);
        authService.signRequest(token, request);
        return authService.execute(request);
    }

    public Response executeWithBody(Verb verb, String uri, OAuth2AccessToken token, HashMap<String, String> body) throws IOException, ExecutionException, InterruptedException {
        OAuthRequest request = new OAuthRequest(verb, uri);
        body.forEach(request::addBodyParameter);
        authService.signRequest(token, request);
        return authService.execute(request);
    }

    public HhListDto<HashMap<String, ?>> executeBody(Verb verb, String uri, OAuth2AccessToken token) throws IOException, ExecutionException, InterruptedException {
        return getMapper().readValue(execute(verb, uri, token).getBody(), HhListDto.class);
    }

    public <T> T executeObject(Verb verb, String uri, OAuth2AccessToken token, Class<T> type) throws IOException, ExecutionException, InterruptedException {
        String body = execute(verb, uri, token).getBody();
        return getMapper().readValue(body, type);

    }

    public OAuth2AccessToken requestAccessToken(String code) throws IOException, ExecutionException, InterruptedException {
        logger.info("Request access token code=" + code);
        return authService.getAccessToken(code);
    }

    public OAuth2AccessToken requestRefreshToken(String refreshToken) throws IOException, ExecutionException, InterruptedException {
        logger.info("Request access token refresh=" + refreshToken);
        return authService.refreshAccessToken(refreshToken);
    }

    public String getConnectionString() {
        return authService.getAuthorizationUrl();
    }

    public <T> HhListDto<T> getObjects(Verb verb, String uri, OAuth2AccessToken token, Class<T> type) throws IOException, ExecutionException, InterruptedException {
        HhListDto<HashMap<String, ?>> body = executeBody(verb, uri, token);
        HhListDto<T> result = new HhListDto<>();
        result.setPage(body.getPage());
        result.setFound(body.getFound());
        result.setPages(body.getPages());
        result.setPerPage(body.getPerPage());
        result.setItems(getEntityList(body, type));
        return result;

    }

    public <T> HhListDto<T> getAllPagesObject(Verb verb, String uri, OAuth2AccessToken token, Class<T> type) throws IOException, ExecutionException, InterruptedException {
        var result = getObjects(verb, uri, token, type);
        var resultList = new HhListDto<T>();
        resultList.setItems(result.getItems());
        log.info("Received " + type + " found: " + result.getFound() + " pages: " + result.getPages() + " page: " + result.getPage() + " perPage: " + result.getPerPage());
        for (int i = 1; i < result.getPages(); i++) {
            String uriPageble = addUriPageParameter(uri, i);
            result = getObjects(verb, uriPageble, token, type);
            resultList.getItems().addAll(result.getItems());
        }
        return resultList;
    }

    public String addUriPageParameter(String uri, int page) {
        if (uri.endsWith("?") || uri.endsWith("&")) {
            return uri + "page=" + page;
        }
        if (uri.contains("?")) {
            return uri + "&page=" + page;
        }
        return uri + "?page=" + page;
    }

    private <T> List<T> getEntityList(HhListDto<HashMap<String, ?>> vacancyEntityHhlistDto, Class<T> type) {
        List<T> resultList = new ArrayList<>();
        if (vacancyEntityHhlistDto.getItems() == null) {
            return resultList;
        }
        vacancyEntityHhlistDto.getItems().forEach(vacancyEntity -> {
            T vacancy = getHhObject(vacancyEntity, type);
            resultList.add(vacancy);
        });
        return resultList;
    }

    public <T> T getHhObject(Object getMap, Class<T> type) {
        HashMap<String, ?> map = (HashMap<String, String>) getMap;
        if (map == null || map.isEmpty()) {
            return createInstance(type);
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(map, type);
    }

    private <T> T createInstance(Class<T> type) {
        try {
            return type.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }


    public <T> T get(String url, Class<T> type) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
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

    public HttpHeaders postGetCookies(String url, MultiValueMap<String, String> params) {
        ResponseEntity<String> response = getResponseEntity(url, params);
        if (response == null) {
            return null;
        }
        return response.getHeaders();
    }

    private ResponseEntity<String> getResponseEntity(String url, MultiValueMap<String, String> params) {
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(params, getHeaders());
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            logger.info("received data: " + response.getBody());
            logger.info("received headers: " + response.getHeaders());
            return response;
        } catch (HttpClientErrorException m) {
            logger.info("Request:" + request + " Received error: " + m.getLocalizedMessage());
            return null;
        }

    }


    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/x-www-form-urlencoded"));
        headers.set("User-Agent", config.getHhUserAgent());
        logger.info("created headers=" + headers);
        return headers;
    }

}
