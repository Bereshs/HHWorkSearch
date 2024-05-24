package ru.bereshs.hhworksearch.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import ru.bereshs.hhworksearch.config.KafkaProducerConfig;
import ru.bereshs.hhworksearch.config.SchedulerConfig;
import ru.bereshs.hhworksearch.controller.AuthorizationController;
import ru.bereshs.hhworksearch.controller.ManagementController;
import ru.bereshs.hhworksearch.hhapiclient.HeadHunterClient;
import ru.bereshs.hhworksearch.producer.KafkaProducerImpl;
import ru.bereshs.hhworksearch.repository.SkillsEntityRepository;
import ru.bereshs.hhworksearch.domain.SkillEntity;
import ru.bereshs.hhworksearch.hhapiclient.dto.HhSimpleListDto;
import ru.bereshs.hhworksearch.hhapiclient.dto.HhVacancyDto;

import java.util.ArrayList;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(SkillsEntityService.class)
@ActiveProfiles("Test")

class SkillsEntityServiceTest {
    @MockBean
    KafkaProducerConfig kafkaProducerConfig;
    @MockBean
    SchedulerConfig schedulerConfig;
    @MockBean
    AuthorizationController authorizationController;
    @MockBean
    AuthorizationService authorizationService;
    @MockBean
    HeadHunterClient headHunterClient;
    @MockBean
    KafkaProducerImpl kafkaProducer;
    @MockBean
    ManagementController managementController;


    @Autowired
    SkillsEntityService skillsEntityService;

    @MockBean
    SkillsEntityRepository skillsEntityRepository;

    @Test
    void extractVacancySkillsTest() {
        List<SkillEntity> skills = new ArrayList<>();

        skills.add(new SkillEntity("java"));
        skills.add(new SkillEntity("maven"));
        skills.add(new SkillEntity("git"));
        skills.add(new SkillEntity("английский"));

        HhVacancyDto vacancy = new HhVacancyDto();
        vacancy.setName("Java разработчик");
        vacancy.setDescription("Нам важно занание maven");
        List<HhSimpleListDto> vacancySkills = new ArrayList<>();
        HhSimpleListDto vacancySkillsDto = new HhSimpleListDto();
        vacancySkillsDto.setName("git");
        vacancySkills.add(vacancySkillsDto);

        vacancy.setSkills(vacancySkills);
        Mockito.when(skillsEntityRepository.findAll()).thenReturn(skills);
        List<SkillEntity> calculateSkills = skillsEntityService.extractVacancySkills(vacancy);

        assertTrue(calculateSkills.toString().contains("java"));
        assertTrue(calculateSkills.toString().contains("git"));
        assertTrue(calculateSkills.toString().contains("maven"));
        assertFalse(calculateSkills.toString().contains("английский"));
    }
}