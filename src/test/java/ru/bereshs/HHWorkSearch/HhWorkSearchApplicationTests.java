package ru.bereshs.HHWorkSearch;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import ru.bereshs.HHWorkSearch.config.KafkaProducerConfig;
import ru.bereshs.HHWorkSearch.config.SchedulerConfig;
import ru.bereshs.HHWorkSearch.controller.AuthorizationController;
import ru.bereshs.HHWorkSearch.controller.ManagementController;
import ru.bereshs.HHWorkSearch.hhApiClient.HeadHunterClient;
import ru.bereshs.HHWorkSearch.producer.KafkaProducerImpl;
import ru.bereshs.HHWorkSearch.service.AuthorizationService;

@ActiveProfiles("Test")
@SpringBootTest
class HhWorkSearchApplicationTests {
	@MockBean
	KafkaProducerConfig kafkaProducerConfig;
	@MockBean
	SchedulerConfig schedulerConfig;
	@MockBean
	AuthorizationController authorizationController;
	@MockBean
	AuthorizationService authorizationService;
	@MockBean
	HeadHunterClient headHunterClient;
	@MockBean
	KafkaProducerImpl kafkaProducer;
	@MockBean
	ManagementController managementController;
//its only for test
	@Test
	void contextLoads() {
	}

}
