package com.psybrainy.CallFlowManager;

import com.psybrainy.CallFlowManager.config.RedisTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(RedisTestConfig.class)
class CallFlowManagerApplicationTests {

	@Test
	void contextLoads() {
	}

}
