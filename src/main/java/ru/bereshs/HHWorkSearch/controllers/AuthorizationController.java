package ru.bereshs.HHWorkSearch.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import ru.bereshs.HHWorkSearch.config.AppConfig;
import ru.bereshs.HHWorkSearch.hhApiClient.HeadHunterClient;
import ru.bereshs.HHWorkSearch.hhApiClient.dto.ResponseToken;
import ru.bereshs.HHWorkSearch.model.AuthorizationService;
import ru.bereshs.HHWorkSearch.model.data.KeyEntity;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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
    public String authorizationPage(String code, Model model){
        KeyEntity key = authorizationService.getByClientId(config.getHhClientId());
        KeyEntity authorizedKey = key;
        if(key.isExpired() ) {
            key.setAuthorizationCode(code);
            authorizationService.save(key);
            authorizedKey = authorizationService.getToken(key);
        }

        model.addAttribute("responseToken", authorizedKey);
        return "/authorized";
    }


    @GetMapping("/authorized")
    public String authorezedPahe(Model model) {
        KeyEntity key = authorizationService.getByClientId(config.getHhClientId());
        KeyEntity authorizedKey = authorizationService.getToken(key);

        model.addAttribute("responseToken", authorizedKey);
        return "/authorized";
    }
}
