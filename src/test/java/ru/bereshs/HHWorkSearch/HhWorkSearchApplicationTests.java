package ru.bereshs.HHWorkSearch;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import ru.bereshs.HHWorkSearch.config.KafkaProducerConfig;
import ru.bereshs.HHWorkSearch.config.SchedulerConfig;

@ActiveProfiles("Test")
@SpringBootTest
class HhWorkSearchApplicationTests {
	@MockBean
	KafkaProducerConfig kafkaProducerConfig;

	@MockBean
	SchedulerConfig schedulerConfig;
	@Test
	void contextLoads() {
	}

}
