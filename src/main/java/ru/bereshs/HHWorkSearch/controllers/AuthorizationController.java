package ru.bereshs.HHWorkSearch.controllers;

import com.github.scribejava.apis.HHApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.bereshs.HHWorkSearch.config.AppConfig;
import ru.bereshs.HHWorkSearch.hhApiClient.HeadHunterClient;
import ru.bereshs.HHWorkSearch.hhApiClient.dto.HhListDto;
import ru.bereshs.HHWorkSearch.hhApiClient.dto.HhUserDto;
import ru.bereshs.HHWorkSearch.model.AuthorizationService;
import ru.bereshs.HHWorkSearch.model.data.KeyEntity;
import ru.bereshs.HHWorkSearch.model.data.ResumeEntity;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

@Controller
public class AuthorizationController {

    private final AuthorizationService authorizationService;
    private final AppConfig config;

    private final HeadHunterClient client;


    @Autowired
    public AuthorizationController(AuthorizationService authorizationService, AppConfig config, HeadHunterClient client) {
        this.authorizationService = authorizationService;
        this.config = config;
        this.client = client;
    }

    @GetMapping("/authorization")
    public String authorizationPage(String code, Model model) throws IOException, ExecutionException, InterruptedException {
        OAuth2AccessToken token = client.requestAccessToken(code);
        OAuthRequest request = new OAuthRequest(Verb.GET, "https://api.hh.ru/me");
        client.getAuthService().signRequest(token, request);
        Response response = client.getAuthService().execute(request);

        KeyEntity key = authorizationService.getByClientId(config.getHhClientId());
        key.set(token);
        key.setAuthorizationCode(code);
        key.setClientId(config.getHhClientId());

        authorizationService.save(key);

        model.addAttribute("accessToken", token);

        model.addAttribute("page", response.getBody());
        return "/authorized";
    }


    @GetMapping("/authorized")
    public String authorizedPage(Model model) throws IOException, ExecutionException, InterruptedException {
        KeyEntity key = authorizationService.getByClientId(config.getHhClientId());
        OAuth2AccessToken token = authorizationService.getToken(key);

        Response response = authorizationService.execute(Verb.GET, "https://api.hh.ru/me");

        model.addAttribute("accessToken", token);

        HashMap<String,?> list = authorizationService.getMapBody(response.getBody());
        HhUserDto hhUserDto = new HhUserDto();
        hhUserDto.set(list);
        model.addAttribute("hhUserDto", hhUserDto);

        return "/authorized";
    }

    @GetMapping("/list")
    public String listPage() {
        HhListDto<LinkedHashMap<String, String>> vacancyDtoHhListDto = client.get("http://localhost:8020" + config.getHhResume(), HhListDto.class);

        String json = vacancyDtoHhListDto.getItems().get(0).get("last_name");
        Logger.getLogger("sss").info("s " + json);
        return "/index";
    }
}
