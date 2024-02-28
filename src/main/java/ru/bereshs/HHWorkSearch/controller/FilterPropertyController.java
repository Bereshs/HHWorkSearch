package ru.bereshs.HHWorkSearch.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.bereshs.HHWorkSearch.Repository.FilterEntityRepository;
import ru.bereshs.HHWorkSearch.domain.FilterEntity;
import ru.bereshs.HHWorkSearch.domain.dto.FilterDto;
import ru.bereshs.HHWorkSearch.service.FilterEntityService;

import java.util.List;

@RestController
@AllArgsConstructor
public class FilterPropertyController {
    private final FilterEntityService filterEntityService;


    @GetMapping("/api/filter")
    public List<FilterEntity> getAllFilter() {

        return filterEntityService.getAll();
    }

    @PostMapping ("/api/filter")
    public String addFilter(@RequestBody FilterDto filterDto) {
        filterEntityService.addToFilter(filterDto);
        return "ok";
    }

    @DeleteMapping("/api/filter")
    public String removeFilter(@RequestBody FilterDto filterDto) {
        filterEntityService.removeFromFilter(filterDto);
        return "ok";
    }

}
