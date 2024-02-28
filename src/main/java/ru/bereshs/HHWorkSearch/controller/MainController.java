package ru.bereshs.HHWorkSearch.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.bereshs.HHWorkSearch.config.AppConfig;
import ru.bereshs.HHWorkSearch.exception.HhWorkSearchException;
import ru.bereshs.HHWorkSearch.hhApiClient.HeadHunterClient;
import ru.bereshs.HHWorkSearch.service.AuthorizationService;
import ru.bereshs.HHWorkSearch.domain.KeyEntity;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
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
