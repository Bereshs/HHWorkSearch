package ru.bereshs.HHWorkSearch.controller;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.bereshs.HHWorkSearch.config.AppConfig;
import ru.bereshs.HHWorkSearch.exception.HhWorkSearchException;
import ru.bereshs.HHWorkSearch.hhApiClient.HeadHunterClient;
import ru.bereshs.HHWorkSearch.hhApiClient.dto.HhListDto;
import ru.bereshs.HHWorkSearch.hhApiClient.dto.HhResumeDto;
import ru.bereshs.HHWorkSearch.hhApiClient.dto.HhUserDto;
import ru.bereshs.HHWorkSearch.service.AuthorizationService;
import ru.bereshs.HHWorkSearch.service.HhService;
import ru.bereshs.HHWorkSearch.service.VacancyEntityService;
import ru.bereshs.HHWorkSearch.domain.KeyEntity;
import ru.bereshs.HHWorkSearch.hhApiClient.dto.HhVacancyDto;
import ru.bereshs.HHWorkSearch.domain.VacancyEntity;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import static java.util.Objects.isNull;

@Controller
@AllArgsConstructor
public class AuthorizationController {

    private final AuthorizationService authorizationService;
    private final AppConfig config;
    private final HeadHunterClient client;
    private final HhService service;

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
    public String authorizedPage(Model model, @RequestParam(required = false) Integer page) throws IOException, ExecutionException, InterruptedException, HhWorkSearchException {
        OAuth2AccessToken token = authorizationService.getToken();
        Response response = client.execute(Verb.GET, "https://api.hh.ru/me", token);
        model.addAttribute("accessToken", token);
        HhListDto<HhResumeDto> myResumeList = service.getActiveResumes(token);

        HashMap<String, ?> list = authorizationService.getMapBody(response.getBody());
        HhUserDto hhUserDto = new HhUserDto();
        hhUserDto.set(list);

        HhListDto<HhVacancyDto> vacancyList = service.getPageRecommendedVacancy(token, 0);
        vacancyList.setItems(vacancyList.getItems());
        model.addAttribute("hhUserDto", hhUserDto);
        model.addAttribute("resumeList", myResumeList.getItems());
        model.addAttribute("vacancyList", vacancyList);
        return "/authorized";
    }


}
