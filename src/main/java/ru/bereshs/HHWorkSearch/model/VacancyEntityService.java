package ru.bereshs.HHWorkSearch.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.bereshs.HHWorkSearch.hhApiClient.dto.HhVacancyDto;
import ru.bereshs.HHWorkSearch.model.data.VacancyEntity;
import ru.bereshs.HHWorkSearch.model.data.VacancyStatus;
import ru.bereshs.HHWorkSearch.model.storage.VacancyEntityRepository;
import ru.bereshs.HHWorkSearch.model.storage.VacancyStatusEntityRepository;

@Service
public class VacancyEntityService {

    private final VacancyEntityRepository vacancyEntityRepository;

    private final VacancyStatusEntityRepository vacancyStatusEntityRepository;

    @Autowired
    public VacancyEntityService(VacancyEntityRepository vacancyEntityRepository, VacancyStatusEntityRepository vacancyStatusEntityRepository) {
        this.vacancyEntityRepository = vacancyEntityRepository;
        this.vacancyStatusEntityRepository = vacancyStatusEntityRepository;
    }

    public void save(HhVacancyDto vacancy, VacancyStatus status){
        VacancyEntity vacancyEntity = vacancyEntityRepository.getByHhId(vacancy.getId());

    }
}
