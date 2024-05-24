package ru.bereshs.hhworksearch.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.bereshs.hhworksearch.config.AppConfig;
import ru.bereshs.hhworksearch.service.AuthorizationService;
import ru.bereshs.hhworksearch.domain.KeyEntity;

import java.util.logging.Logger;

@Controller
@AllArgsConstructor
public class MainController {

    private final AppConfig config;

    private final AuthorizationService authorizationService;

    @GetMapping("/")
    public String mainPage(Model model) {
        KeyEntity key = authorizationService.getByClientId(config.getHhClientId());
        Logger.getLogger("info").info("key= " + key.isValid());
        if (key.isValid()) {
            return "redirect:/authorized";
        }

        model.addAttribute("connectionString", authorizationService.getConnectionString());
        return "/index";
    }
}
