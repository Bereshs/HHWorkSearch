package ru.bereshs.HHWorkSearch.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.Verb;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.bereshs.HHWorkSearch.config.AppConfig;
import ru.bereshs.HHWorkSearch.hhApiClient.HeadHunterClient;
import ru.bereshs.HHWorkSearch.hhApiClient.dto.HhVacancyDto;
import ru.bereshs.HHWorkSearch.model.AuthorizationService;
import ru.bereshs.HHWorkSearch.model.data.KeyEntity;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Controller
public class VacancyController {

    private final AppConfig config;
    private final AuthorizationService authorizationService;
    private final HeadHunterClient client;

    public VacancyController(AppConfig config, AuthorizationService authorizationService, HeadHunterClient client) {
        this.config = config;
        this.authorizationService = authorizationService;
        this.client = client;
    }


    @GetMapping("/vacancy/{id}")
    String requestResumePage(@PathVariable String id, Model model) throws IOException, ExecutionException, InterruptedException {
        KeyEntity key = authorizationService.getByClientId(config.getHhClientId());
        OAuth2AccessToken token = authorizationService.getToken(key);
        String url = "https://api.hh.ru/vacancies/" + id;

        String body = client.execute(Verb.GET, url, token).getBody();
        ObjectMapper mapper = new ObjectMapper();

        HhVacancyDto vacancy = mapper.readValue(body,  HhVacancyDto.class);
        model.addAttribute("vacancy", vacancy);
        return "/vacancy";
    }

}
