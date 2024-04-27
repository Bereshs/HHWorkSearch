package ru.bereshs.HHWorkSearch.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.test.context.ActiveProfiles;
import ru.bereshs.HHWorkSearch.config.KafkaProducerConfig;
import ru.bereshs.HHWorkSearch.config.SchedulerConfig;
import ru.bereshs.HHWorkSearch.repository.FilterEntityRepository;
import ru.bereshs.HHWorkSearch.domain.FilterEntity;
import ru.bereshs.HHWorkSearch.domain.VacancyEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@WebMvcTest(FilterEntityService.class)
@ActiveProfiles("Test")

class FilterEntityServiceTest {
    @MockBean
    KafkaProducerConfig kafkaProducerConfig;
    @MockBean
    SchedulerConfig schedulerConfig;


    @Autowired
    private FilterEntityService filterEntityService;

    @MockBean
    private FilterEntityRepository filterEntityRepository;

    private List<FilterEntity> filterEntityListName;
    private List<FilterEntity> filterEntityListExperience;

    private VacancyEntity vacancy;

    @BeforeEach
    void setup() {
        filterEntityListName = new ArrayList<>();
        FilterEntity filterEntity = new FilterEntity();
        filterEntity.setScope("name");
        filterEntity.setWord("kotlin");
        filterEntityListName.add(filterEntity);

        filterEntityListExperience = new ArrayList<>();
        filterEntity = new FilterEntity();
        filterEntity.setScope("experience");
        filterEntity.setWord("between1and3");
        filterEntityListExperience.add(filterEntity);

        vacancy = new VacancyEntity();
        vacancy.setName("Kotlin разработчик");
        vacancy.setExperience("between1and3");

    }

    @Test
    void doFilterTest() {
        List<VacancyEntity> vacancyEntities = new ArrayList<>();
        VacancyEntity vacancyEntity = new VacancyEntity();
        vacancyEntity.setName("Java разработчик");
        vacancyEntity.setExperience("between1and3");
        vacancyEntities.add(vacancyEntity);
        vacancyEntity = new VacancyEntity();
        vacancyEntity.setName("разработчик");
        vacancyEntity.setExperience("between3and6");
        vacancyEntities.add(vacancyEntity);
        vacancyEntities.add(vacancy);

        Mockito.when(filterEntityRepository.findFilterEntityByScope("name"))
                .thenReturn(filterEntityListName);
        Mockito.when(filterEntityRepository.findFilterEntityByScope("experience"))
                .thenReturn(filterEntityListExperience);

        List<VacancyEntity> filteredList = filterEntityService.doFilterNameAndExperience(vacancyEntities);

        assertEquals(1, filteredList.size());
        assertEquals("разработчик", filteredList.get(0).getName());


    }

    @Test
    void isValidTest() {
        Mockito.when(filterEntityRepository.findFilterEntityByScope("name"))
                .thenReturn(filterEntityListName);
        Mockito.when(filterEntityRepository.findFilterEntityByScope("experience"))
                .thenReturn(filterEntityListExperience);

        assertFalse(filterEntityService.isValid(vacancy));

    }

    @Test
    void containsExcludeWordsTest() {

        Mockito.when(filterEntityRepository.findFilterEntityByScope("name"))
                .thenReturn(filterEntityListName);
        Mockito.when(filterEntityRepository.findFilterEntityByScope("experience"))
                .thenReturn(filterEntityListExperience);

        assertTrue(filterEntityService.isContainWordsScope(vacancy.getName(), filterEntityRepository.findFilterEntityByScope("name")));
        assertTrue(filterEntityService.isContainWordsScope(vacancy.getExperience(), filterEntityRepository.findFilterEntityByScope("experience")));

    }
}