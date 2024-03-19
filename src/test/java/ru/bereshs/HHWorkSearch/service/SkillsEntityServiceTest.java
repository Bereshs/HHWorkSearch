package ru.bereshs.HHWorkSearch.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.bereshs.HHWorkSearch.Repository.SkillsEntityRepository;
import ru.bereshs.HHWorkSearch.domain.SkillEntity;
import ru.bereshs.HHWorkSearch.hhApiClient.dto.HhVacancyDto;
import ru.bereshs.HHWorkSearch.hhApiClient.dto.HhVacancySkillsDto;

import java.util.ArrayList;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(SkillsEntityService.class)
class SkillsEntityServiceTest {

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
        List<HhVacancySkillsDto> vacancySkills = new ArrayList<>();
        HhVacancySkillsDto vacancySkillsDto = new HhVacancySkillsDto();
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