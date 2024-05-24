package ru.bereshs.hhworksearch.domain;

import ru.bereshs.hhworksearch.hhapiclient.dto.HhSalaryDto;
import ru.bereshs.hhworksearch.hhapiclient.dto.HhSimpleListDto;

import java.util.List;

public interface FilteredVacancy {
    String getName();

    String getExperience();

    String getDescription();

    List<String> getSkillStringList();

    HhSimpleListDto getEmployer();

    HhSalaryDto getSalary();

}
