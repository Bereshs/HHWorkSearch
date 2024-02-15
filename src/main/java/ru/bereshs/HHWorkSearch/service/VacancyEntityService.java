package ru.bereshs.HHWorkSearch.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.bereshs.HHWorkSearch.hhApiClient.dto.HhVacancyDto;
import ru.bereshs.HHWorkSearch.domain.VacancyEntity;
import ru.bereshs.HHWorkSearch.Repository.VacancyEntityRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class VacancyEntityService {

    private final VacancyEntityRepository vacancyEntityRepository;

    private final EmployerEntityService employerEntityService;

    @Autowired
    public VacancyEntityService(VacancyEntityRepository vacancyEntityRepository, EmployerEntityService employerEntityService) {
        this.vacancyEntityRepository = vacancyEntityRepository;
        this.employerEntityService = employerEntityService;
    }

    public VacancyEntity getByHhId(String hhId) {
        return vacancyEntityRepository.getByHhId(hhId);
    }

    public VacancyEntity getFirstByCreatedAt() {
        Sort sort = Sort.by("CreatedAt").descending();
        return vacancyEntityRepository.findFirstBy(sort);
    }

    public List<VacancyEntity> getUnique(List<VacancyEntity> vacancyEntityList) {

        var unuque=new ArrayList<>(
                vacancyEntityList.stream().filter(
                        vacancyEntity -> vacancyEntityRepository.getByHhId(vacancyEntity.getHhId()) == null
                ).toList());
    unuque.forEach(ele->{
        String id= ele.getHhId();
        log.info("id='"+id+"'");

    });

    return unuque;
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

    public VacancyEntity getById(String id) {
        return vacancyEntityRepository.getByHhId(id);
    }


}
