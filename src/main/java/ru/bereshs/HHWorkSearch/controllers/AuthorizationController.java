package ru.bereshs.HHWorkSearch.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.bereshs.HHWorkSearch.config.AppConfig;
import ru.bereshs.HHWorkSearch.hhApiClient.HeadHunterClient;
import ru.bereshs.HHWorkSearch.hhApiClient.dto.HhListDto;
import ru.bereshs.HHWorkSearch.model.AuthorizationService;
import ru.bereshs.HHWorkSearch.model.data.KeyEntity;
import ru.bereshs.HHWorkSearch.model.data.ResumeEntity;

import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
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
    public String authorizationPage(String code, Model model) {
        KeyEntity key = authorizationService.getByClientId(config.getHhClientId());
        KeyEntity authorizedKey = key;
        if (key.isExpired()) {
            key.setAuthorizationCode(code);
            authorizationService.save(key);
            authorizedKey = authorizationService.getToken(key);
        }

        long seconds = Timestamp.valueOf(authorizedKey.getTime()).getTime() + authorizedKey.getExpiresIn() * 1000;
        List<ResumeEntity> resumeList=client.getUserResumeEntityList();

        Date tokenExpireTime = new Date(seconds);
        model.addAttribute("date", tokenExpireTime);
        model.addAttribute("responseToken", authorizedKey);
        model.addAttribute("resumeList", resumeList);
        model.addAttribute("vacancyList", client.getVacancyEntityList());
        return "/authorized";
    }


    @GetMapping("/authorized")
    public String authorizedPage(Model model) {
        KeyEntity key = authorizationService.getByClientId(config.getHhClientId());
        KeyEntity authorizedKey = authorizationService.getToken(key);

        model.addAttribute("responseToken", authorizedKey);
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
