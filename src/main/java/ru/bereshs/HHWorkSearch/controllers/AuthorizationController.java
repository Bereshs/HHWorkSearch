package ru.bereshs.HHWorkSearch.controllers;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.bereshs.HHWorkSearch.config.AppConfig;
import ru.bereshs.HHWorkSearch.hhApiClient.HeadHunterClient;
import ru.bereshs.HHWorkSearch.hhApiClient.dto.HhListDto;
import ru.bereshs.HHWorkSearch.hhApiClient.dto.HhResumeDto;
import ru.bereshs.HHWorkSearch.hhApiClient.dto.HhUserDto;
import ru.bereshs.HHWorkSearch.model.AuthorizationService;
import ru.bereshs.HHWorkSearch.model.data.KeyEntity;
import ru.bereshs.HHWorkSearch.model.data.ResumeEntity;
import ru.bereshs.HHWorkSearch.hhApiClient.dto.HhVacancyDto;

import java.io.IOException;
import java.util.HashMap;
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
        Response response = client.execute(Verb.GET, "https://api.hh.ru/me", token);

        Response resume = client.execute(Verb.GET, "https://api.hh.ru/resumes/mine", token);
        model.addAttribute("accessToken", token);

        HhListDto<HhResumeDto> myResumeList = client.getObjects(Verb.GET, "https://api.hh.ru/resumes/mine", token, HhResumeDto.class);

        HashMap<String, ?> list = authorizationService.getMapBody(response.getBody());
        HhUserDto hhUserDto = new HhUserDto();
        hhUserDto.set(list);
        String uri = "https://api.hh.ru/resumes/" + myResumeList.getItems().get(0).getId() + "/similar_vacancies?responses_count_enabled=true" +
                "&period=1" +
                "&order_by=publication_time" +
                "&vacancy_search_fields=name" +
                "&text=java" +
                "&per_page=100";

        HhListDto<HhVacancyDto> vacancyList = client.getObjects(Verb.GET, uri, token, HhVacancyDto.class);
        model.addAttribute("hhUserDto", hhUserDto);
        model.addAttribute("resumeList", myResumeList.getItems());
        model.addAttribute("vacancyList", vacancyList);
        return "/authorized";
    }

}
