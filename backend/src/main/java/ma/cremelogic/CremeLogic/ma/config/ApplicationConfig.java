package ma.cremelogic.CremeLogic.ma.config;

import ma.cremelogic.CremeLogic.ma.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Configuration
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class ApplicationConfig {

    private final UtilisateurRepository repository;

    @Autowired
    public ApplicationConfig(UtilisateurRepository repository) {
        this.repository = repository;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> repository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

}
