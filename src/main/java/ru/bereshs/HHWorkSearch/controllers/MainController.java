package ru.bereshs.HHWorkSearch.controllers;

import com.github.scribejava.apis.HHApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.oauth.OAuth20Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.bereshs.HHWorkSearch.config.AppConfig;
import ru.bereshs.HHWorkSearch.hhApiClient.HeadHunterClient;
import ru.bereshs.HHWorkSearch.model.AuthorizationService;
import ru.bereshs.HHWorkSearch.model.data.KeyEntity;

@Controller
public class MainController {
    private final AppConfig config;
    private final HeadHunterClient headHunterClient;


    private final AuthorizationService authorizationService;

    @Autowired
    public MainController(AppConfig config, HeadHunterClient headHunterClient, AuthorizationService authorizationService) {
        this.config = config;
        this.headHunterClient = headHunterClient;
        this.authorizationService = authorizationService;
    }


    @GetMapping("/")
    public String mainPage(Model model) {
        KeyEntity key = authorizationService.getByClientId(config.getHhClientId());

        if(key==null) {
            model.addAttribute("connectionString", authorizationService.getConnectionString());
            return "/index";
        }

        return "redirect:/authorized";

    }
}
