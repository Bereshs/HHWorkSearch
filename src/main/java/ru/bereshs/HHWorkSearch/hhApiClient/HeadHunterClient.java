package ru.bereshs.HHWorkSearch.hhApiClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import ru.bereshs.HHWorkSearch.config.AppConfig;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

@Service
public class HeadHunterClient {
    private final AppConfig config;
    private final Logger logger = Logger.getLogger(HeadHunterClient.class.getName());

    @Autowired
    public HeadHunterClient(AppConfig config) {
        this.config = config;
    }


    public static <T> T get(URL url, Class<T> type) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(url, type);
    }

    public <T> T post(String url, MultiValueMap<String, String> params, Class<T> type) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/x-www-form-urlencoded"));
        headers.set("User-Agent", config.getHhUserAgent());
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(params, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<T> response = restTemplate.postForEntity(url, request, type);
        if(response.getStatusCode().is4xxClientError()) {
            logger.info("Received error: "+response.getBody());
            return null;
        }
        return response.getBody();
    }
}
