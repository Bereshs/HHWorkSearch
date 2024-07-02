package ru.bereshs.hhworksearch.config;

import com.github.scribejava.core.model.OAuth2AccessToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import ru.bereshs.hhworksearch.domain.*;
import ru.bereshs.hhworksearch.hhapiclient.HhLocalDateTime;
import ru.bereshs.hhworksearch.hhapiclient.dto.HhVacancyDto;
import ru.bereshs.hhworksearch.producer.KafkaProducer;
import ru.bereshs.hhworksearch.service.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class SchedulerConfig {

    private final SchedulerService service;

    @Scheduled(cron = "0 0 9-18 * * *")
    public void scheduleDayLightTask() throws IOException, ExecutionException, InterruptedException {
        service.dailyLightTaskRequest();
    }

    @Scheduled(cron = "0 30 19 * * *")
    public void scheduleDailyFullRequest() throws InterruptedException, IOException, ExecutionException {
        service.dailyFullRequest();
    }

    @Scheduled(cron = "0 30 18 * * *")
    public void scheduleDailyRecommendedRequest() throws IOException, ExecutionException, InterruptedException {
        service.dailyRecommendedRequest();
    }


}
