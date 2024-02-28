package ru.bereshs.HHWorkSearch.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.bereshs.HHWorkSearch.Repository.FilterEntityRepository;
import ru.bereshs.HHWorkSearch.domain.FilterEntity;
import ru.bereshs.HHWorkSearch.domain.FilteredVacancy;
import ru.bereshs.HHWorkSearch.domain.VacancyEntity;
import ru.bereshs.HHWorkSearch.domain.dto.FilterDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

@Service
public class FilterEntityService {
    private final FilterEntityRepository filterEntityRepository;

    @Autowired
    public FilterEntityService(FilterEntityRepository filterEntityRepository) {
        this.filterEntityRepository = filterEntityRepository;
    }

    public List<FilterEntity> getAll() {
        return filterEntityRepository.findAll();
    }

    public List<VacancyEntity> doFilter(List<VacancyEntity> vacancyEntities) {
        return vacancyEntities.stream().filter(this::isValid).toList();
    }

    public boolean isValid(FilteredVacancy filteredVacancy) {
        boolean is = !isContainsExcludeWords(filteredVacancy, "name")
                && !isContainsExcludeWords(filteredVacancy, "experience");
        return is;
    }

    boolean isContainsExcludeWords(FilteredVacancy filteredVacancy, String field) {
        List<FilterEntity> scopeName = filterEntityRepository.findFilterEntityByScope(field);
        for (FilterEntity name : scopeName) {
            String searchString = field.equals("name") ? filteredVacancy.getName().toLowerCase() : filteredVacancy.getExperience().toLowerCase();
            if (searchString.contains(name.getWord())) {
                return true;
            }
        }
        return false;
    }

    public boolean containsWords(String line, List<FilterEntity> scope) {
        for (FilterEntity filter : scope) {
            if (line.toLowerCase().contains(filter.getWord().toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public void addToFilter(FilterDto filterDto) {
        List<FilterEntity> filterEntities = getListFromDto(filterDto);
        filterEntities.forEach(this::addIfNotExist);
    }

    public void addIfNotExist(FilterEntity filterEntity) {
        var optional = filterEntityRepository.findFilterEntityByScopeAndWord(filterEntity.getScope(), filterEntity.getWord());
        if (optional.isEmpty()) {
            save(filterEntity);
        }
    }

    private void save(FilterEntity filterEntity) {
        filterEntityRepository.save(filterEntity);
    }

    public void removeFromFilter(FilterDto filterDto) {
        List<FilterEntity> filterEntities = getListFromDto(filterDto);
        filterEntityRepository.deleteAll(
                getListFromRepository(filterEntities)
        );
    }

    public List<FilterEntity> getListFromRepository(List<FilterEntity> filterEntities) {
        return filterEntities.stream().map(element -> filterEntityRepository.findFilterEntityByScopeAndWord(element.getScope(), element.getWord()).orElse(null)).filter(Objects::nonNull).toList();
    }

    private List<FilterEntity> getListFromDto(FilterDto filterDto) {
        List<String> listWords = filterDto.getWords();
        return listWords.stream().map(word -> {
            FilterEntity filterEntity = new FilterEntity();
            filterEntity.setScope(filterDto.getScope().toLowerCase());
            filterEntity.setWord(word.toLowerCase());
            return filterEntity;
        }).toList();
    }
}
