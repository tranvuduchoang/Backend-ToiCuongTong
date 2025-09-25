package com.toicuongtong.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class BackendApplicationTests {

	@Test
	void contextLoads() {
		// Simple test that just verifies the application context loads
		// This will fail if there are any configuration issues
	}

}
