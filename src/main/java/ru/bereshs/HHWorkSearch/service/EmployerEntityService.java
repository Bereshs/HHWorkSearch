package ru.bereshs.HHWorkSearch.service;

import org.springframework.stereotype.Service;
import ru.bereshs.HHWorkSearch.hhApiClient.dto.HhEmployerDto;
import ru.bereshs.HHWorkSearch.domain.Employer;
import ru.bereshs.HHWorkSearch.Repository.EmployerEntityRepository;

@Service
public class EmployerEntityService {

    private final EmployerEntityRepository employerEntityRepository;

    public EmployerEntityService(EmployerEntityRepository employerEntityRepository) {
        this.employerEntityRepository = employerEntityRepository;
    }

    Employer getByHhId(String hhId) {
        return employerEntityRepository.getByHhId(hhId);
    }

    Employer getByEmployerDto (HhEmployerDto employerDto) {
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

}
