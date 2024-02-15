package ru.bereshs.HHWorkSearch.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.Verb;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.bereshs.HHWorkSearch.config.AppConfig;
import ru.bereshs.HHWorkSearch.exception.HhWorkSearchException;
import ru.bereshs.HHWorkSearch.hhApiClient.HeadHunterClient;
import ru.bereshs.HHWorkSearch.hhApiClient.dto.HhVacancyDto;
import ru.bereshs.HHWorkSearch.service.AuthorizationService;
import ru.bereshs.HHWorkSearch.model.VacancyStatusEntityService;
import ru.bereshs.HHWorkSearch.domain.KeyEntity;
import ru.bereshs.HHWorkSearch.domain.VacancyStatus;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Controller
public class VacancyController {

    private final AppConfig config;
    private final AuthorizationService authorizationService;
    private final HeadHunterClient client;

    private final VacancyStatusEntityService vacancyStatusEntityService;


    public VacancyController(AppConfig config, AuthorizationService authorizationService, HeadHunterClient client, VacancyStatusEntityService vacancyStatusEntityService) {
        this.config = config;
        this.authorizationService = authorizationService;
        this.client = client;
        this.vacancyStatusEntityService = vacancyStatusEntityService;
    }


    @GetMapping("/vacancy/{id}")
    String viewVacancyPage(@PathVariable String id, Model model) throws IOException, ExecutionException, InterruptedException, HhWorkSearchException {
        HhVacancyDto vacancy = getVacancyDto(id);
        model.addAttribute("vacancy", vacancy);
        setViewedVacancy(vacancy.getId());

        return "/vacancy";
    }

    @GetMapping("/vacancy/request/{id}")
    String requestVacancyPage(@PathVariable String id) throws IOException, ExecutionException, InterruptedException, HhWorkSearchException {
        HhVacancyDto vacancy = getVacancyDto(id);
        setViewedVacancy(vacancy.getId());

        return "redirect:" + vacancy.getUrl();
    }

    public void setViewedVacancy(String hhId) {
        vacancyStatusEntityService.setStatus(hhId, VacancyStatus.view);
    }

    public HhVacancyDto getVacancyDto(String id) throws IOException, ExecutionException, InterruptedException, HhWorkSearchException {
        KeyEntity key = authorizationService.getByClientId(config.getHhClientId());
        OAuth2AccessToken token = authorizationService.getToken(key);
        String url = "https://api.hh.ru/vacancies/" + id;
        String body = client.execute(Verb.GET, url, token).getBody();
        ObjectMapper mapper = new ObjectMapper();
        HhVacancyDto vacancy = mapper.readValue(body, HhVacancyDto.class);
        return vacancy;
    }

}
