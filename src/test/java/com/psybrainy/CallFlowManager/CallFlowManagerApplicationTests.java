package com.psybrainy.CallFlowManager;

import com.psybrainy.CallFlowManager.config.RedisTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.test.context.EmbeddedKafka;

@SpringBootTest
@Import(RedisTestConfig.class)
@EmbeddedKafka(partitions = 1, topics = "call-topic")
class CallFlowManagerApplicationTests {

	@Test
	void contextLoads() {
	}

}
