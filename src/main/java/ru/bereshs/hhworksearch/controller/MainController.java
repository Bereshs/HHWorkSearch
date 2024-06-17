package ru.bereshs.hhworksearch.controller;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.bereshs.hhworksearch.config.AppConfig;
import ru.bereshs.hhworksearch.hhapiclient.dto.HhListDto;
import ru.bereshs.hhworksearch.hhapiclient.dto.HhUserDto;
import ru.bereshs.hhworksearch.hhapiclient.dto.HhVacancyDto;
import ru.bereshs.hhworksearch.service.AuthorizationService;
import ru.bereshs.hhworksearch.domain.KeyEntity;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

@Controller
@AllArgsConstructor
public class MainController {

    private final AppConfig config;

    private final AuthorizationService authorizationService;

    @GetMapping("/")
    public String mainPage(Model model) throws IOException, ExecutionException, InterruptedException {
        KeyEntity key = authorizationService.getByClientId(config.getHhClientId());

        if (key.isValid()) {
            HhUserDto hhUserDto = new HhUserDto();
            model.addAttribute("hhUserDto", hhUserDto);
            return "redirect:/authorized";
        }

        model.addAttribute("connectionString", authorizationService.getConnectionString());
        return "/index";
    }
}
