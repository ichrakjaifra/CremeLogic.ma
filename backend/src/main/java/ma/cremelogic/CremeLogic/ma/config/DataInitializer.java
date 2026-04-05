package ma.cremelogic.CremeLogic.ma.config;

import ma.cremelogic.CremeLogic.ma.entity.Utilisateur;
import ma.cremelogic.CremeLogic.ma.enums.Role;
import ma.cremelogic.CremeLogic.ma.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Créer un utilisateur administrateur s'il n'existe pas
        if (utilisateurRepository.findByEmail("admin@patisserie.com").isEmpty()) {
            Utilisateur admin = Utilisateur.builder()
                    .nom("Admin")
                    .prenom("System")
                    .email("admin@patisserie.com")
                    .motDePasse(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .telephone("0600000000")
                    .actif(true)
                    .build();
            utilisateurRepository.save(admin);
            System.out.println("Admin user created: admin@patisserie.com / admin123");
        }

        // Créer un utilisateur chef s'il n'existe pas
        if (utilisateurRepository.findByEmail("chef@patisserie.com").isEmpty()) {
            Utilisateur chef = Utilisateur.builder()
                    .nom("Chef")
                    .prenom("Patissier")
                    .email("chef@patisserie.com")
                    .motDePasse(passwordEncoder.encode("chef123"))
                    .role(Role.CHEF)
                    .telephone("0600000001")
                    .actif(true)
                    .build();
            utilisateurRepository.save(chef);
            System.out.println("Chef user created: chef@patisserie.com / chef123");
        }

        // Créer un utilisateur magasinier s'il n'existe pas
        if (utilisateurRepository.findByEmail("magasinier@patisserie.com").isEmpty()) {
            Utilisateur magasinier = Utilisateur.builder()
                    .nom("Magasinier")
                    .prenom("Stock")
                    .email("magasinier@patisserie.com")
                    .motDePasse(passwordEncoder.encode("mag123"))
                    .role(Role.MAGASINIER)
                    .telephone("0600000002")
                    .actif(true)
                    .build();
            utilisateurRepository.save(magasinier);
            System.out.println("Magasinier user created: magasinier@patisserie.com / mag123");
        }

        // Créer un utilisateur employé s'il n'existe pas
        if (utilisateurRepository.findByEmail("employe@patisserie.com").isEmpty()) {
            Utilisateur employe = Utilisateur.builder()
                    .nom("Employé")
                    .prenom("Standard")
                    .email("employe@patisserie.com")
                    .motDePasse(passwordEncoder.encode("emp123"))
                    .role(Role.EMPLOYE)
                    .telephone("0600000003")
                    .actif(true)
                    .build();
            utilisateurRepository.save(employe);
            System.out.println("Employé user created: employe@patisserie.com / emp123");
        }
    }
}
