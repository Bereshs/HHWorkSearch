package ru.bereshs.hhworksearch.controller;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.bereshs.hhworksearch.config.AppConfig;
import ru.bereshs.hhworksearch.exception.HhWorkSearchException;
import ru.bereshs.hhworksearch.hhapiclient.impl.HeadHunterClientRestTemplate;
import ru.bereshs.hhworksearch.hhapiclient.dto.HhListDto;
import ru.bereshs.hhworksearch.hhapiclient.dto.HhResumeDto;
import ru.bereshs.hhworksearch.hhapiclient.dto.HhUserDto;
import ru.bereshs.hhworksearch.service.AuthorizationService;
import ru.bereshs.hhworksearch.service.FilterEntityService;
import ru.bereshs.hhworksearch.service.HhService;
import ru.bereshs.hhworksearch.domain.KeyEntity;
import ru.bereshs.hhworksearch.hhapiclient.dto.HhVacancyDto;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

@Controller
@AllArgsConstructor
public class AuthorizationController {

    private final AuthorizationService authorizationService;
    private final AppConfig config;
    private final HeadHunterClientRestTemplate client;
    private final HhService service;
    private final FilterEntityService filterEntityService;

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
        return "authorized";
    }


    @RequestMapping ("/authorized")
    public String authorizedPage(Model model, @RequestParam(required = false) Integer page) throws IOException, ExecutionException, InterruptedException, HhWorkSearchException {
        OAuth2AccessToken token = authorizationService.getToken();
        Response response = client.execute(Verb.GET, "https://api.hh.ru/me", token);
        model.addAttribute("accessToken", token);
        HhListDto<HhResumeDto> myResumeList = service.getActiveResumes(token);

        HashMap<String, ?> list = authorizationService.getMapBody(response.getBody());
        HhUserDto hhUserDto = new HhUserDto();

        hhUserDto.set(list);

        String key =  filterEntityService.getKey();
        HhListDto<HhVacancyDto> vacancyList = service.getPageRecommendedVacancy(token, 0, key);
        vacancyList.setItems(vacancyList.getItems());
        model.addAttribute("hhUserDto", hhUserDto);
        model.addAttribute("resumeList", myResumeList.getItems());
        model.addAttribute("vacancyList", vacancyList);
        return "authorized";
    }


}
