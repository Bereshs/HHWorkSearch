package ru.bereshs.hhworksearch.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.bereshs.hhworksearch.aop.Loggable;
import ru.bereshs.hhworksearch.domain.VacancyStatus;
import ru.bereshs.hhworksearch.domain.dto.DailyReportDto;
import ru.bereshs.hhworksearch.hhapiclient.dto.HhVacancyDto;
import ru.bereshs.hhworksearch.domain.VacancyEntity;
import ru.bereshs.hhworksearch.repository.VacancyEntityRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class VacancyEntityService {

    private final VacancyEntityRepository vacancyEntityRepository;
    public String getDaily() {
        var vacancyEntities = vacancyEntityRepository.getVacancyEntitiesByTimeStampAfter(LocalDateTime.now().minusDays(1));
        DailyReportDto dailyReportDto = new DailyReportDto(vacancyEntities);
        return dailyReportDto.toString();
    }

    public Optional<VacancyEntity> getByHhId(String hhId) {
        return vacancyEntityRepository.getByHhId(hhId);
    }

    @Loggable
    public List<HhVacancyDto> getUnique(List<HhVacancyDto> vacancyList) {
        return vacancyList.stream().filter(element -> vacancyEntityRepository.getByHhId(element.getId()).isEmpty()).toList();
    }

    public void updateTimeStamp(List<HhVacancyDto> vacancyEntityList) {
        vacancyEntityList.forEach(this::updateVacancyTimeStamp);
    }

    private void updateVacancyTimeStamp(HhVacancyDto element) {
        var vacancyOpt = getByHhId(element.getId());
        vacancyOpt.ifPresent(vacancy -> updateResponses(vacancy, element.getCounters().getTotalResponses()));
    }

    private void updateResponses(VacancyEntity vacancy, int responses) {
        vacancy.setStatus(VacancyStatus.UPDATED);
        vacancy.setResponses(responses);
        save(vacancy);
    }

    public void saveAll(List<HhVacancyDto> vacancyEntityList) {
        for (HhVacancyDto element : vacancyEntityList) {
            VacancyEntity vacancy = getByVacancyDto(element);
            save(vacancy);
        }
    }

    public VacancyEntity getByVacancyDto(HhVacancyDto vacancyDto) {
        var vacancyOpt = getById(vacancyDto.getId());
        return vacancyOpt.orElseGet(() -> createNewVacancy(vacancyDto));
    }

    private VacancyEntity createNewVacancy(HhVacancyDto vacancyDto) {
        VacancyEntity vacancy = new VacancyEntity(vacancyDto);
        vacancyEntityRepository.save(vacancy);
        return vacancy;
    }

    public void updateVacancyStatusFromList(List<VacancyEntity> list) {
        list.forEach(element -> {
            Optional<VacancyEntity> entity = getByHhId(element.getHhId());
            if (entity.isPresent()) {
                VacancyEntity vacancyExt = entity.get();
                if (!vacancyExt.getStatus().equals(element.getStatus())) {
                    vacancyExt.setStatus(element.getStatus());
                    save(vacancyExt);
                }
            }
        });
    }

    public Optional<VacancyEntity> getById(String id) {
        return vacancyEntityRepository.getByHhId(id);
    }

    public void save(VacancyEntity vacancy) {
        vacancy.setTimeStamp(LocalDateTime.now());
        vacancyEntityRepository.save(vacancy);
    }

    public void changeAllStatus(List<HhVacancyDto> list, VacancyStatus status) {
        list.forEach(element -> changeVacancyStatus(element, status));
    }

    private void changeVacancyStatus(HhVacancyDto element, VacancyStatus status) {
        var vacancyOpt = getByHhId(element.getId());
        vacancyOpt.ifPresent(vacancy -> {
            vacancy.setStatus(status);
            save(vacancy);
        });
    }

}
