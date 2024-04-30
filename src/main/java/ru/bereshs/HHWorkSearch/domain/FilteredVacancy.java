package ru.bereshs.HHWorkSearch.domain;

import ru.bereshs.HHWorkSearch.hhApiClient.dto.HhSalaryDto;
import ru.bereshs.HHWorkSearch.hhApiClient.dto.HhSimpleListDto;

import java.util.List;

public interface FilteredVacancy {
    String getName();

    String getExperience();

    String getDescription();

    List<String> getSkillStringList();

    HhSimpleListDto getEmployer();

    HhSalaryDto getSalary();

}
