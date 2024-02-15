package ru.bereshs.HHWorkSearch.controller;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
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
public class AuthorizationController {

    private final AuthorizationService authorizationService;
    private final AppConfig config;

    private final HeadHunterClient client;

    private final VacancyEntityService vacancyEntityService;

    @Autowired
    public AuthorizationController(AuthorizationService authorizationService, AppConfig config, HeadHunterClient client, VacancyEntityService vacancyEntities) {
        this.authorizationService = authorizationService;
        this.config = config;
        this.client = client;
        this.vacancyEntityService = vacancyEntities;
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
    public String authorizedPage(Model model, @RequestParam(required = false) Integer page) throws IOException, ExecutionException, InterruptedException, HhWorkSearchException {
        KeyEntity key = authorizationService.getByClientId(config.getHhClientId());
        OAuth2AccessToken token = authorizationService.getToken(key);
        Response response = client.execute(Verb.GET, "https://api.hh.ru/me", token);
        model.addAttribute("accessToken", token);
        HhListDto<HhResumeDto> myResumeList = client.getObjects(Verb.GET, "https://api.hh.ru/resumes/mine", token, HhResumeDto.class);


        HashMap<String, ?> list = authorizationService.getMapBody(response.getBody());
        HhUserDto hhUserDto = new HhUserDto();
        hhUserDto.set(list);

        String uri = getVacancyConnectionString(page);
        HhListDto<HhVacancyDto> vacancyList = getPagebleVanacyList(page, token);//client.getObjects(Verb.GET, uri, token, HhVacancyDto.class);
        vacancyList.setItems(excludeWords(vacancyList.getItems(), token));
        model.addAttribute("hhUserDto", hhUserDto);
        model.addAttribute("resumeList", myResumeList.getItems());
        model.addAttribute("vacancyList", vacancyList);
        return "/authorized";
    }

    private List<HhVacancyDto> excludeWords(List<HhVacancyDto> vacancyDtos, OAuth2AccessToken token) throws IOException, ExecutionException, InterruptedException {
        List<HhVacancyDto> newList = new ArrayList<>();
        for (HhVacancyDto vacancyDto : vacancyDtos) {
            if (vacancyDto.isValid()) {
                vacancyDto.convertDate();
                String url = "https://api.hh.ru/vacancies/" + vacancyDto.getId();
                HhVacancyDto vacancyFull = client.executeObject(Verb.GET, url, token, HhVacancyDto.class);
                if (vacancyFull.isValid()) {
                    vacancyDto.setDescription(vacancyFull.getDescription());
                }
                vacancyDto.setUrlRequest("/vacancy/" + vacancyDto.getId());
                newList.add(vacancyDto);
            }
        }
        return newList;
    }


    private HhListDto<HhVacancyDto> getPagebleVanacyList(Integer page, OAuth2AccessToken token) throws IOException, ExecutionException, InterruptedException {
        HhListDto<HhVacancyDto> vacancyList = new HhListDto<>();
        String uri = getVacancyConnectionString(page);
        HhListDto<HhVacancyDto> tempList = client.getObjects(Verb.GET, uri, token, HhVacancyDto.class);
        vacancyList.setPages(1);
        vacancyList.setPerPage(100);
        vacancyList.setPage(1);
        vacancyList.setItems(excludeWords(tempList.getItems(), token));
        if (isNull(page)) {
            page = 1;
        }
        for (int counter = page; counter < tempList.getPages(); counter++) {
            String uriNextPage = getVacancyConnectionString(counter);
            Logger.getLogger("logger").info("Avalible pages " + tempList.getPages() + " current page " + tempList.getPage());
            tempList = client.getObjects(Verb.GET, uriNextPage, token, HhVacancyDto.class);
            vacancyList.getItems().addAll(excludeWords(tempList.getItems(), token));
            LocalDateTime timeHhVacancy = vacancyList.getItems().get(0).getCreatedAt();
            VacancyEntity firstElement = vacancyEntityService.getFirstByCreatedAt();
            LocalDateTime timeDbVacancy = LocalDateTime.now().minusDays(1);

            if (firstElement != null) {
                timeDbVacancy = firstElement.getPublished();
            }

            if (timeDbVacancy != null && timeHhVacancy != null && timeDbVacancy.isBefore(timeHhVacancy)) {
                vacancyEntityService.getUnique(vacancyList.getItems().stream().map(vacancyEntityService::getByVacancyDto).toList());
            } else {
                break;
            }
        }
        vacancyList.setFound(vacancyList.getItems().size());
        return vacancyList;
    }

    private String getVacancyConnectionString(Integer page) {
        String uri = "https://api.hh.ru/vacancies?responses_count_enabled=true" +
                "&period=1" +
                "&order_by=publication_time" +
                "&vacancy_search_fields=name" +
                "&text=java" +
                "&per_page=100";
        if (!isNull(page) && page > 0) {
            uri += "&page=" + page;
        }
        return uri;
    }
}
