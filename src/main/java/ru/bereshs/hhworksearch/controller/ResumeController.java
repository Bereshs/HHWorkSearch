package ru.bereshs.hhworksearch.controller;

import com.github.scribejava.core.model.OAuth2AccessToken;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.bereshs.hhworksearch.domain.MessageEntity;
import ru.bereshs.hhworksearch.domain.ResumeEntity;
import ru.bereshs.hhworksearch.domain.SkillEntity;
import ru.bereshs.hhworksearch.exception.HhWorkSearchException;
import ru.bereshs.hhworksearch.hhapiclient.HhLocalDateTime;
import ru.bereshs.hhworksearch.hhapiclient.dto.*;
import ru.bereshs.hhworksearch.service.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@AllArgsConstructor
@Slf4j
@Tag(name = "Резюме",
        description = "Работа с резюме")
public class ResumeController {

    private final HhService service;
    private final AuthorizationService authorizationService;
    private final RatingEmployerService ratingEmployerService;
    private final SkillsEntityService skillsEntityService;
    private final NegotiationsService messageEntityService;
    private final ResumeEntityService resumeEntityService;


    @Operation(summary = "Отправка списка скилов")
    @PostMapping("/api/resume/skill_set")
    public String postSkillSet(@RequestBody List<SkillEntity> skills) {
        skillsEntityService.saveAll(skills);
        return "ok";
    }

    @Operation(summary = "Изменение скила")
    @PatchMapping("/api/resume/skill_set/{id}")
    public String patchSkillEntityById(@PathVariable long id, @RequestBody SkillEntity entityDto) throws HhWorkSearchException {
        skillsEntityService.patchSkillEntityById(id, entityDto);
        return "ok";
    }

    @Operation(summary = "Получение списка скилов")
    @GetMapping("/api/resume/skill_set")
    public HhListDto<SkillEntity> getSkillsSet() {
        return skillsEntityService.getAll();
    }


    @Operation(summary = "Добавления сопроводительного письма")
    @PostMapping("/api/resume/message")
    public String postMessage(@RequestBody MessageEntity messageDto) {
        messageEntityService.save(messageDto);
        return "ok";
    }

    @Operation(summary = "Изменение сопроводительного письма")
    @PatchMapping("/api/resume/message/{id}")
    public String patchMessageById(@RequestBody MessageEntity messageDto, @PathVariable long id) throws HhWorkSearchException {
        messageEntityService.patchMessageById(id, messageDto);
        return "ok";
    }

    @Operation(summary = "Получение сопроводительного письма для резюме")
    @GetMapping("/api/resume/{resumeId}/message/{messageId}")
    public String getMessageById(@PathVariable long messageId, @PathVariable String resumeId) throws HhWorkSearchException, IOException, ExecutionException, InterruptedException {
        MessageEntity message = messageEntityService.getMessageById(messageId);
        HhResumeDto resumeDto = service.getResumeById(resumeId, getToken());
        List<SkillEntity> skills = new ArrayList<>();
        for (String skill : resumeDto.getSkillSet()) {
            var elements = skill.split(" ");
            for (String element : elements) {
                skills.add(new SkillEntity(element.toLowerCase()));
            }
        }
        skillsEntityService.updateList(skills);
        String vacancyName = "Демонстрационная вакансия";
        return message.getMessage(skills, vacancyName);
    }

    @Operation(summary = "Поиск лояльного работодателя")
    @GetMapping("/api/resume/loyalEmployer")
    public Map<HhSimpleListDto, Double> getLoyalEmployer() throws IOException, ExecutionException, InterruptedException {
        var resumes = service.getActiveResumes(getToken());
        var loyalEmployer = service.getLoyalEmployer(getToken(), resumes.getItems().get(0).getId());
        var difference = ratingEmployerService.getDifferenceAndUpdate(loyalEmployer);
        log.info("difference=" + difference);
        return loyalEmployer;
    }

    @PostMapping("/api/resume/{id}/publish")
    public String updateResume(@PathVariable String id) throws IOException, ExecutionException, InterruptedException {
        ResumeEntity resume = resumeEntityService.getByHhid(id);
        if (resume.getNextPublish() == null) {
            var resumeDto = service.getResumeById(id, getToken());
            resume.setNextPublish(HhLocalDateTime.decodeLocalData(resumeDto.getNextPublishAt()));
        }

        if (resume.getNextPublish().isBefore(LocalDateTime.now())) {
            service.updateResume(getToken(), id);
        }

        var resumeDto = service.getResumeById(id, getToken());
        resume.setNextPublish(HhLocalDateTime.decodeLocalData(resumeDto.getNextPublishAt()));
        resumeEntityService.save(resume);


        return "ok";
    }

    @Operation(summary = "Просмотры моего резюме")
    @GetMapping("/api/resume/views")
    public HhListDto<HhViewsResume> getViewsResumeList() throws IOException, ExecutionException, InterruptedException {

        var resumes = service.getActiveResumes(getToken());
        HhListDto<HhViewsResume> list = new HhListDto<>();
        list.setItems(new ArrayList<>());
        for (HhResumeDto resume : resumes.getItems()) {
            list.getItems().addAll(
                    service.getHhViewsResumeDtoList(getToken(), resume.getId()).getItems()
            );
        }
        return list;
    }

    @Operation(summary = "Обновление резюме с hh.ru")
    @GetMapping("/api/resume/mine")
    public HhListDto<HhResumeDto> getMineResumes() throws IOException, ExecutionException, InterruptedException {
        HhListDto<HhResumeDto> resumeList = service.getActiveResumes(getToken());
        List<ResumeEntity> resumeEntities = resumeList.getItems().stream().map(resumeEntityService::getByHhResumeDto).toList();
        for (ResumeEntity resumeEntity : resumeEntities) {
            var accessTypes = service.getResumeAccessType(resumeEntity.getHhId(), getToken());
            resumeEntity.setAccessType(accessTypes);
            log.info(resumeEntity.getHhId() + " " + accessTypes);
        }
        resumeEntityService.saveAll(resumeEntities);
        return resumeList;
    }


    @Operation(summary = "Установка резюме по умолчанию")
    @PostMapping("/api/resume/default")
    public String setDefaultResume(@RequestBody HhResumeDto resumeDto) {
        ResumeEntity resume = resumeEntityService.getByHhid(resumeDto.getId());
        resume.setDefault(true);
        resumeEntityService.save(resume);

        return "ok";
    }

    @Operation(summary = "Просмотр резюме")
    @GetMapping("/api/resume/{resumeId}")
    public HhResumeDto getResumeById(@PathVariable String resumeId) throws IOException, ExecutionException, InterruptedException {
        return service.getResumeById(resumeId, getToken());
    }

    private OAuth2AccessToken getToken() throws IOException, ExecutionException, InterruptedException {
        return authorizationService.getToken();
    }
}
