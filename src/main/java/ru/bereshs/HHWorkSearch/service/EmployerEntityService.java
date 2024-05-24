package ru.bereshs.HHWorkSearch.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.bereshs.HHWorkSearch.domain.Employer;
import ru.bereshs.HHWorkSearch.hhApiClient.dto.HhVacancyDto;
import ru.bereshs.HHWorkSearch.repository.EmployerEntityRepository;
import ru.bereshs.HHWorkSearch.hhApiClient.dto.HhSimpleListDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployerEntityService {

    private final EmployerEntityRepository employerEntityRepository;


    Employer getByHhId(String hhId) {
        return employerEntityRepository.getByHhId(hhId);
    }

    Employer getByEmployerDto (HhSimpleListDto employerDto) {
        Employer employer = employerEntityRepository.getByHhId(String.valueOf(employerDto.getId()));
        if(employer==null) {
            employer=new Employer();
            employer.setHhId(String.valueOf(employerDto.getId()));
            employer.setName(employerDto.getName());
            employer.setUrl(employer.getUrl());
            employerEntityRepository.save(employer);
        }

        return employer;
    }

    public  List<Employer> extractEmployers(List<HhVacancyDto> list) {
        return list.stream().map(entity->new Employer(entity.getEmployer())).toList();
    }

    public void saveAll(List<Employer> list) {
        list.stream().filter(employer->!employerEntityRepository.existsByHhId(employer.getHhId()))
                .forEach(employerEntityRepository::save);
    }


}
