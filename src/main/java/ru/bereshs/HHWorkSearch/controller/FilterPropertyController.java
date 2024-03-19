package ru.bereshs.HHWorkSearch.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.bereshs.HHWorkSearch.Repository.FilterEntityRepository;
import ru.bereshs.HHWorkSearch.domain.FilterEntity;
import ru.bereshs.HHWorkSearch.domain.dto.FilterDto;
import ru.bereshs.HHWorkSearch.service.FilterEntityService;

import java.util.List;

@RestController
@AllArgsConstructor
@Tag(   name = "Фильтры",
        description = "Работа с фильтрами для вакансий")
public class FilterPropertyController {
    private final FilterEntityService filterEntityService;


    @Operation(summary = "Получение списка всех фильтров")
    @GetMapping("/api/filter")
    public List<FilterEntity> getAllFilter() {

        return filterEntityService.getAll();
    }

    @Operation(summary = "Запись нового фильтра")
    @PostMapping ("/api/filter")
    public String addFilter(@RequestBody FilterDto filterDto) {
        filterEntityService.addToFilter(filterDto);
        return "ok";
    }

    @Operation(summary = "Удаление фильтра")
    @DeleteMapping("/api/filter")
    public String removeFilter(@RequestBody FilterDto filterDto) {
        filterEntityService.removeFromFilter(filterDto);
        return "ok";
    }

}
