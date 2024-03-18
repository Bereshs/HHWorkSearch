package ru.bereshs.HHWorkSearch.domain;

import java.util.List;

public interface FilteredVacancy {
    String getName();

    String getExperience();

    String getDescription();

    List<String> getSkillStringList();

}
