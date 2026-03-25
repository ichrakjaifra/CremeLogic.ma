package ma.cremelogic.CremeLogic.ma;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
	"SPRING_DATASOURCE_URL=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
	"SPRING_DATASOURCE_USERNAME=sa",
	"SPRING_DATASOURCE_PASSWORD=",
	"JWT_SECRET=9a4f2c8d3b7a1e5f8g9h0i1j2k3l4m5n6o7p8q9r0s1t2u3v4w5x6y7z8A9B0C1D",
	"spring.profiles.active=test",
	"spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
	"spring.datasource.driver-class-name=org.h2.Driver",
	"spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
	"spring.jpa.hibernate.ddl-auto=create-drop"
})
@ActiveProfiles("test")
class ApplicationTests {

	@Test
	void contextLoads() {
	}

}
