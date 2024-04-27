package ru.bereshs.HHWorkSearch.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.bereshs.HHWorkSearch.repository.RatingEmployerRepository;
import ru.bereshs.HHWorkSearch.domain.RatingEmployer;
import ru.bereshs.HHWorkSearch.hhApiClient.dto.HhSimpleListDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class RatingEmployerService {
    private final RatingEmployerRepository ratingEmployerRepository;

    public RatingEmployerService(RatingEmployerRepository ratingEmployerRepository) {
        this.ratingEmployerRepository = ratingEmployerRepository;
    }

    public List<RatingEmployer> getDifferenceAndUpdate(Map<HhSimpleListDto, Double> ratingEmployerMap) {
        List<RatingEmployer> difference = new ArrayList<>();
        for (var entry : ratingEmployerMap.entrySet()) {
            var current = findByHhEmployerDto(entry.getKey());
            if (!current.getRating().equals(entry.getValue())) {
                difference.add(new RatingEmployer(entry.getKey().getId(), entry.getValue()));
            }
        }
        updateAll(difference);
        return difference;
    }

    public RatingEmployer findByHhEmployerDto(HhSimpleListDto hhEmployerDto) {
        return ratingEmployerRepository.findByEmployerId(hhEmployerDto.getId()).orElse(new RatingEmployer(hhEmployerDto.getId(), 0D));
    }
    public void save(RatingEmployer ratingEmployer) {
        ratingEmployerRepository.save(ratingEmployer);
    }

    public void updateAll(List<RatingEmployer> ratingEmployers) {
        for (RatingEmployer ratingEmployer : ratingEmployers) {
            var current = ratingEmployerRepository.findByEmployerId(ratingEmployer.getEmployerId()).orElse(new RatingEmployer(ratingEmployer.getEmployerId(), 0D));
            log.info("current: "+current+" rating: "+ratingEmployer);
            if (!current.getRating().equals(ratingEmployer.getRating())) {
                current.setRating(ratingEmployer.getRating());
                save(current);
            }
        }
    }
}
