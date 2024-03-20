package ru.bereshs.HHWorkSearch.controller;

import com.github.scribejava.core.model.OAuth2AccessToken;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.bereshs.HHWorkSearch.domain.*;
import ru.bereshs.HHWorkSearch.exception.HhWorkSearchException;
import ru.bereshs.HHWorkSearch.hhApiClient.dto.HhVacancyDto;
import ru.bereshs.HHWorkSearch.service.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@AllArgsConstructor
@Slf4j

@Tag(   name = "Вакансии",
        description = "Работа с вакансиями")
public class VacancyController {

    private final AuthorizationService authorizationService;
    private final SkillsEntityService skillsEntityService;
    private final NegotiationsService messageEntityService;
    private final VacancyEntityService vacancyEntityService;
    private final FilterEntityService filterEntityService;
    private final HhService service;

    @Operation(summary = "Рекомендованные мне вакансии")
    @GetMapping("/api/vacancy/recommended")
    public List<VacancyEntity> getRecommendedVacancyList() throws IOException, ExecutionException, InterruptedException {
        var vacancyList = getVacancyEntityList();
        var filteredList = filterEntityService.doFilter(vacancyList);

        return saveUniqueList(filteredList);
    }


    @Operation(summary = "Просмотр вакансии")
    @GetMapping("/api/vacancy/{id}")
    public HhVacancyDto viewVacancyPage(@PathVariable String id) throws IOException, ExecutionException, InterruptedException {
        return service.getVacancyById(id, getToken());
    }


    @Operation(summary = "Сопроводительное письмо для вакансии")
    @GetMapping("/api/vacancy/{id}/message")
    public String getVacancyMessage(@PathVariable String id) throws IOException, ExecutionException, InterruptedException, HhWorkSearchException {
        HhVacancyDto vacancyDto = service.getVacancyById(id, getToken());
        MessageEntity message = messageEntityService.getMessageById(1);
        List<SkillEntity> skills = skillsEntityService.getSkillEntityList(vacancyDto);
        return message.getMessage(
                skillsEntityService.updateList(skills),
                vacancyDto.getName()
        );
    }

    private OAuth2AccessToken getToken() throws IOException, ExecutionException, InterruptedException {
        return authorizationService.getToken();
    }


    private List<VacancyEntity> getVacancyEntityList() throws IOException, ExecutionException, InterruptedException {
        return service.getRecommendedVacancy(getToken()).stream().map(VacancyEntity::new).toList();
    }

    private List<VacancyEntity> saveUniqueList(List<VacancyEntity> list) {
        var unique = vacancyEntityService.getUnique(list);
        log.info("save unique list size: " + unique.size());
        vacancyEntityService.saveAll(unique);
        return unique;
    }

    private HhVacancyDto getVacancyById(String id) {
        try {
            return service.getVacancyById(id, getToken());
        } catch (IOException | ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
