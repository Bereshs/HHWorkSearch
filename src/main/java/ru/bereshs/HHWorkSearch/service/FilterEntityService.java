package ru.bereshs.HHWorkSearch.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.bereshs.HHWorkSearch.Repository.FilterEntityRepository;
import ru.bereshs.HHWorkSearch.domain.*;
import ru.bereshs.HHWorkSearch.domain.dto.FilterDto;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;


@Service
public class FilterEntityService{
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

    public List<VacancyEntity> doFilterDescription(List<VacancyEntity> vacancyEntities) {
        return vacancyEntities.stream().filter(this::isValidDescription).toList();
    }

    public boolean isValid(FilteredVacancy filteredVacancy) {
        return !isContainsExcludeWordsScope(filteredVacancy.getName(), getScope(FilterScope.Name))
                && !isContainsExcludeWordsScope(filteredVacancy.getExperience(), getScope(FilterScope.Experience));
    }

    public boolean isValidDescription(VacancyEntity filteredVacancy) {
        if (filteredVacancy.getDescription().length() < 10) {
            return false;
        }
        return !isContainsExcludeWordsScope(filteredVacancy.getDescription(), getScope(FilterScope.Description));
    }

    boolean isContainsExcludeWordsScope(String line, List<FilterEntity> scopeName) {
        for (FilterEntity name : scopeName) {
            if (line.toLowerCase().contains(name.getWord())) {
                return true;
            }
        }
        return false;
    }

    List<FilterEntity> getScope(FilterScope scope) {
        return filterEntityRepository.findFilterEntityByScope(scope.name().toLowerCase());
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
