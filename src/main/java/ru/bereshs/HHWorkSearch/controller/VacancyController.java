package ru.bereshs.HHWorkSearch.controller;

import com.github.scribejava.core.model.OAuth2AccessToken;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

@Tag(name = "Вакансии",
        description = "Работа с вакансиями")
public class VacancyController {

    private final AuthorizationService authorizationService;
    private final SkillsEntityService skillsEntityService;
    private final NegotiationsService negotiationsService;
    private final VacancyEntityService vacancyEntityService;
    private final FilterEntityService<HhVacancyDto> filterEntityService;
    private final HhService service;
    private final EmployerEntityService employerEntityService;

    @Operation(summary = "Рекомендованные мне вакансии")
    @GetMapping("/api/vacancy/recommended")
    public List<HhVacancyDto> getRecommendedVacancyList() throws IOException, ExecutionException, InterruptedException {
        var vacancyList = getVacancyEntityList();
        var filteredList = filterEntityService.doFilterNameAndExperience(vacancyList);

        return saveUniqueList(filteredList);
    }


    @Operation(summary = "Просмотр вакансии")
    @GetMapping("/api/vacancy/{id}")
    public HhVacancyDto viewVacancyPage(@PathVariable String id) throws IOException, ExecutionException, InterruptedException {
        return service.getVacancyById(id, getToken());
    }

    @Operation(summary = "Отправка отклика на вакансию")
    @PostMapping("/api/vacancy/{vacancyId}/resume/{resumeId}")
    public String postNegotiation(@PathVariable String vacancyId, @PathVariable String resumeId) throws HhWorkSearchException, IOException, ExecutionException, InterruptedException {
        VacancyEntity vacancyEntity = vacancyEntityService.getById(vacancyId).orElseThrow(() -> new HhWorkSearchException("Wrong vacancyId"));
        if (vacancyEntity.getStatus().equals(VacancyStatus.request)) {
            throw new HhWorkSearchException("Negotiation on vacancy already requested");
        }
        HhVacancyDto vacancy = service.getVacancyById(vacancyId, getToken());
        List<SkillEntity> skills = skillsEntityService.extractVacancySkills(vacancy);
        String negotiationMessage = negotiationsService.getNegotiationMessage(vacancy, skills);

        negotiationsService.doNegotiation(negotiationMessage, resumeId, vacancyId);
        vacancyEntity.setStatus(VacancyStatus.request);
        vacancyEntityService.save(vacancyEntity);
        return "ok";
    }

    @Operation(summary = "Сопроводительное письмо для вакансии")
    @GetMapping("/api/vacancy/{id}/message")
    public String getVacancyMessage(@PathVariable String id) throws IOException, ExecutionException, InterruptedException, HhWorkSearchException {
        HhVacancyDto vacancyDto = service.getVacancyById(id, getToken());
        MessageEntity message = negotiationsService.getMessageById(1);
        List<SkillEntity> skills = skillsEntityService.extractVacancySkills(vacancyDto);
        return message.getMessage(
                skillsEntityService.updateList(skills),
                vacancyDto.getName()
        );
    }

    private OAuth2AccessToken getToken() throws IOException, ExecutionException, InterruptedException {
        return authorizationService.getToken();
    }


    private List<HhVacancyDto> getVacancyEntityList() throws IOException, ExecutionException, InterruptedException {
        String key = filterEntityService.getKey();
        var list = service.getRecommendedVacancy(getToken(), key);
        var employerList = employerEntityService.extractEmployers(list);
        employerEntityService.saveAll(employerList);
        return list;
    }

    private List<HhVacancyDto> saveUniqueList(List<HhVacancyDto> list) {
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
