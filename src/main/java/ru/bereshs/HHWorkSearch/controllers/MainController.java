package ru.bereshs.HHWorkSearch.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.bereshs.HHWorkSearch.config.AppConfig;


@Controller
public class MainController {
    private final AppConfig config;

    @Autowired
    public MainController(AppConfig config) {
        this.config = config;
    }


    @GetMapping("/")
    public String mainPage(Model model) {
        String connectionString = "https://hh.ru/oauth/authorize?" +
                "  response_type=code&" +
                "  client_id="+config.getHhClientId()+"&" +
                "  redirect_uri=http://localhost:8080/authorization";
        model.addAttribute("connectionString", connectionString);
        return "/index";
    }
}
