package ru.bereshs.HHWorkSearch.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import ru.bereshs.HHWorkSearch.config.KafkaProducerConfig;
import ru.bereshs.HHWorkSearch.config.SchedulerConfig;
import ru.bereshs.HHWorkSearch.controller.AuthorizationController;
import ru.bereshs.HHWorkSearch.repository.SkillsEntityRepository;
import ru.bereshs.HHWorkSearch.domain.SkillEntity;
import ru.bereshs.HHWorkSearch.hhApiClient.dto.HhSimpleListDto;
import ru.bereshs.HHWorkSearch.hhApiClient.dto.HhVacancyDto;

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