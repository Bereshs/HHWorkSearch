package ru.bereshs.HHWorkSearch.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.bereshs.HHWorkSearch.domain.FilteredVacancy;
import ru.bereshs.HHWorkSearch.domain.SkillEntity;
import ru.bereshs.HHWorkSearch.hhApiClient.dto.HhVacancyDto;
import ru.bereshs.HHWorkSearch.domain.VacancyEntity;
import ru.bereshs.HHWorkSearch.Repository.VacancyEntityRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class VacancyEntityService {

    private final VacancyEntityRepository vacancyEntityRepository;

    public VacancyEntity getByHhId(String hhId) {
        return vacancyEntityRepository.getByHhId(hhId);
    }

    public VacancyEntity getFirstByCreatedAt() {
        Sort sort = Sort.by("CreatedAt").descending();
        return vacancyEntityRepository.findFirstBy(sort);
    }


    public List<VacancyEntity> getUnique(List<VacancyEntity> vacancyEntityList) {
        return new ArrayList<>(
                vacancyEntityList.stream()
                        .filter(vacancyEntity -> vacancyEntityRepository.getByHhId(vacancyEntity.getHhId()) == null)
                        .toList());
    }

    public void saveAll(List<VacancyEntity> vacancyEntityList) {
        vacancyEntityRepository.saveAll(vacancyEntityList);

    }

    public VacancyEntity getByVacancyDto(HhVacancyDto vacancyDto) {
        VacancyEntity vacancy = getById(vacancyDto.getId());
        if (vacancy == null) {
            vacancy = new VacancyEntity(vacancyDto);
            vacancyEntityRepository.save(vacancy);
        }
        return vacancy;
    }

    public void updateAll(List<VacancyEntity> list) {
        list.forEach(this::update);
    }

    public void update(VacancyEntity vacancy) {
        VacancyEntity vacancyDb = vacancyEntityRepository.getByHhId(vacancy.getHhId());
        vacancyDb.setDescription(vacancyDb.getDescription());
        vacancyDb.setName(vacancyDb.getName());
        save(vacancyDb);
    }

    public VacancyEntity getById(String id) {
        return vacancyEntityRepository.getByHhId(id);
    }

    public void save(VacancyEntity vacancy) {
        vacancy.setTimeStamp(LocalDateTime.now());
        vacancyEntityRepository.save(vacancy);
    }

    public LocalDateTime getLastUpdate() {
        Sort sort = Sort.by("TimeStamp").descending();
        var list = vacancyEntityRepository.findAll(sort);
        return list.isEmpty() ? LocalDateTime.now().minusDays(2) : list.get(0).getTimeStamp();
    }

    public List<VacancyEntity> getVacancyEnityList(List<HhVacancyDto> list) {
        return list.stream().map(VacancyEntity::new).toList();
    }
}
