package ma.cremelogic.CremeLogic.ma;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "ma.cremelogic.CremeLogic.ma")
@EnableJpaRepositories(basePackages = "ma.cremelogic.CremeLogic.ma.repository")
@EntityScan(basePackages = "ma.cremelogic.CremeLogic.ma.entity")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
