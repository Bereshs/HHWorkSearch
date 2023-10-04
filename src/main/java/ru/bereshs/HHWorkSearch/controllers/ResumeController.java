package ru.bereshs.HHWorkSearch.controllers;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.bereshs.HHWorkSearch.config.AppConfig;
import ru.bereshs.HHWorkSearch.hhApiClient.HeadHunterClient;
import ru.bereshs.HHWorkSearch.hhApiClient.dto.HhListDto;
import ru.bereshs.HHWorkSearch.hhApiClient.dto.HhResumeDto;
import ru.bereshs.HHWorkSearch.model.AuthorizationService;
import ru.bereshs.HHWorkSearch.model.data.KeyEntity;
import ru.bereshs.HHWorkSearch.model.data.ResumeEntity;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

@RestController
public class ResumeController {
    private final HeadHunterClient client;
    private final AuthorizationService authorizationService;
    private final AppConfig config;

    public ResumeController(HeadHunterClient client, AuthorizationService authorizationService, AppConfig config) {
        this.client = client;
        this.authorizationService = authorizationService;
        this.config = config;
    }

    @GetMapping("/resume/update")
    ResponseEntity<String> updateResumeHandler() throws IOException, ExecutionException, InterruptedException {
        KeyEntity key = authorizationService.getByClientId(config.getHhClientId());
        OAuth2AccessToken token = authorizationService.getToken(key);


        HhListDto<HhResumeDto> completed = client.getObjects(Verb.GET, "https://api.hh.ru/resumes/mine", token, HhResumeDto.class);
        Logger.getLogger("ssss").info("dd" + completed.getItems().get(0));
        String url = "https://api.hh.ru/resumes/" + completed.getItems().get(0).getId();
        Logger.getLogger("sss").info("ss" + url);

        String body = client.execute(Verb.PUT, url, token).getBody();
        Logger.getLogger("sss").info("bb " + body);

        return ResponseEntity.ok("ok");
    }
}
