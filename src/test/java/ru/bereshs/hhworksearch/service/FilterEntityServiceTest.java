package ru.bereshs.hhworksearch.service;

import org.junit.jupiter.api.BeforeEach;
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
import ru.bereshs.hhworksearch.domain.FilterScope;
import ru.bereshs.hhworksearch.hhapiclient.HeadHunterClient;
import ru.bereshs.hhworksearch.producer.KafkaProducerImpl;
import ru.bereshs.hhworksearch.repository.FilterEntityRepository;
import ru.bereshs.hhworksearch.domain.FilterEntity;
import ru.bereshs.hhworksearch.domain.VacancyEntity;

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
    private FilterEntityService filterEntityService;

    @MockBean
    private FilterEntityRepository filterEntityRepository;

    private List<FilterEntity> filterEntityListName;
    private List<FilterEntity> filterEntityListExperience;

    private VacancyEntity vacancy;
    private final String filterScopeExperienceParameter = FilterScope.EXPERIENCE.toString().toLowerCase();
    private final String experienceDataParameter="between1and3";
    private final String filterScopeNameParameter = FilterScope.NAME.toString().toLowerCase();

    @BeforeEach
    void setup() {
        filterEntityListName = new ArrayList<>();
        FilterEntity filterEntity = new FilterEntity();
        filterEntity.setScope(filterScopeNameParameter);
        filterEntity.setWord("kotlin");
        filterEntityListName.add(filterEntity);

        filterEntityListExperience = new ArrayList<>();
        filterEntity = new FilterEntity();
        filterEntity.setScope(filterScopeExperienceParameter);
        filterEntity.setWord(experienceDataParameter);
        filterEntityListExperience.add(filterEntity);

        vacancy = new VacancyEntity();
        vacancy.setName("Kotlin разработчик");
        vacancy.setExperience(experienceDataParameter);

    }

    @Test
    void doFilterTest() {
        List<VacancyEntity> vacancyEntities = new ArrayList<>();
        VacancyEntity vacancyEntity = new VacancyEntity();
        vacancyEntity.setName("Java разработчик");
        vacancyEntity.setExperience(experienceDataParameter);
        vacancyEntities.add(vacancyEntity);
        vacancyEntity = new VacancyEntity();
        vacancyEntity.setName("разработчик");
        vacancyEntity.setExperience("between3and6");
        vacancyEntities.add(vacancyEntity);
        vacancyEntities.add(vacancy);

        Mockito.when(filterEntityRepository.findFilterEntityByScope(filterScopeNameParameter))
                .thenReturn(filterEntityListName);
        Mockito.when(filterEntityRepository.findFilterEntityByScope(filterScopeExperienceParameter))
                .thenReturn(filterEntityListExperience);

        List<VacancyEntity> filteredList = filterEntityService.doFilterNameAndExperience(vacancyEntities);

        assertEquals(1, filteredList.size());
        assertEquals("разработчик", filteredList.get(0).getName());


    }

    @Test
    void isValidTest() {
        Mockito.when(filterEntityRepository.findFilterEntityByScope(filterScopeNameParameter))
                .thenReturn(filterEntityListName);
        Mockito.when(filterEntityRepository.findFilterEntityByScope(filterScopeExperienceParameter))
                .thenReturn(filterEntityListExperience);

        assertFalse(filterEntityService.isValid(vacancy));

    }

    @Test
    void containsExcludeWordsTest() {

        Mockito.when(filterEntityRepository.findFilterEntityByScope(filterScopeNameParameter))
                .thenReturn(filterEntityListName);
        Mockito.when(filterEntityRepository.findFilterEntityByScope(filterScopeExperienceParameter))
                .thenReturn(filterEntityListExperience);

        assertTrue(filterEntityService.isContainWordsScope(vacancy.getName(), filterEntityRepository.findFilterEntityByScope(filterScopeNameParameter)));
        assertTrue(filterEntityService.isContainWordsScope(vacancy.getExperience(), filterEntityRepository.findFilterEntityByScope(filterScopeExperienceParameter)));

    }
}