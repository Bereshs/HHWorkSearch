package ru.bereshs.HHWorkSearch.model;

import org.springframework.stereotype.Service;
import ru.bereshs.HHWorkSearch.domain.VacancyStatus;
import ru.bereshs.HHWorkSearch.model.data.VacancyStatusEntity;
import ru.bereshs.HHWorkSearch.model.storage.VacancyStatusEntityRepository;

import java.time.LocalDateTime;

@Service
public class VacancyStatusEntityService {
    private final VacancyStatusEntityRepository vacancyStatusEntityRepository;

    public VacancyStatusEntityService(VacancyStatusEntityRepository vacancyStatusEntityRepository) {
        this.vacancyStatusEntityRepository = vacancyStatusEntityRepository;
    }


    public void setStatus(String hhId, VacancyStatus status) {
        VacancyStatusEntity vacancyStatusEntity = getVacancyStatus(hhId);
        vacancyStatusEntity.setStatus(status);
        save(vacancyStatusEntity);
    }

    public VacancyStatusEntity getVacancyStatus(String hhId) {
        VacancyStatusEntity vacancyStatus = vacancyStatusEntityRepository.getByHhVacancyId(hhId);
        if (vacancyStatus == null) {
            vacancyStatus = createNew(hhId);
        }
        return vacancyStatus;
    }


    VacancyStatusEntity createNew(String hhId) {
        VacancyStatusEntity vacancyStatusEntity = new VacancyStatusEntity();
        vacancyStatusEntity.setHhVacancyId(hhId);
        vacancyStatusEntity.setStatus(VacancyStatus.found);
        save(vacancyStatusEntity);
        return vacancyStatusEntity;
    }

    public void save(VacancyStatusEntity vacancyStatusEntity) {
        vacancyStatusEntity.setStatusTime(LocalDateTime.now());
        vacancyStatusEntityRepository.save(vacancyStatusEntity);
    }
}
