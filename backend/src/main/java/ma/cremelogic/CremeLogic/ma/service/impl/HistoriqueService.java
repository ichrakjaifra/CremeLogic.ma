package ma.cremelogic.CremeLogic.ma.service.impl;

import ma.cremelogic.CremeLogic.ma.entity.HistoriqueActivite;
import ma.cremelogic.CremeLogic.ma.entity.Utilisateur;
import ma.cremelogic.CremeLogic.ma.repository.HistoriqueActiviteRepository;
import ma.cremelogic.CremeLogic.ma.repository.UtilisateurRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class HistoriqueService {

    private final HistoriqueActiviteRepository historiqueRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final HttpServletRequest request;

    public void enregistrerCreation(String entite, Long entiteId, String description) {
        enregistrer("CREATE", entite, entiteId, description, null, null);
    }

    public void enregistrerModification(String entite, Long entiteId, String description) {
        enregistrer("UPDATE", entite, entiteId, description, null, null);
    }

    public void enregistrerSuppression(String entite, Long entiteId, String description) {
        enregistrer("DELETE", entite, entiteId, description, null, null);
    }

    public void enregistrerLogin(Long utilisateurId) {
        enregistrer("LOGIN", "UTILISATEUR", utilisateurId, "Connexion au système", null, null);
    }

    public void enregistrerLogout(Long utilisateurId) {
        enregistrer("LOGOUT", "UTILISATEUR", utilisateurId, "Déconnexion du système", null, null);
    }

    private void enregistrer(String typeAction, String entite, Long entiteId, String description,
            String ancienneValeur, String nouvelleValeur) {
        try {
            // Récupérer l'utilisateur connecté
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            Utilisateur utilisateur = utilisateurRepository.findByEmail(email).orElse(null);

            if (utilisateur == null && !"LOGIN".equals(typeAction) && !"LOGOUT".equals(typeAction)) {
                log.warn("Utilisateur non trouvé pour l'historique: {}", email);
                return;
            }

            HistoriqueActivite historique = HistoriqueActivite.builder()
                    .dateAction(LocalDateTime.now())
                    .action(typeAction)
                    .entite(entite)
                    .entiteId(entiteId)
                    .description(description)
                    .ancienneValeur(ancienneValeur)
                    .nouvelleValeur(nouvelleValeur)
                    .utilisateur(utilisateur)
                    .ipAddress(getClientIp())
                    .userAgent(request.getHeader("User-Agent"))
                    .build();

            historiqueRepository.save(historique);
            log.debug("Historique enregistré: {} {} par {}", typeAction, entite, email);

        } catch (Exception e) {
            log.error("Erreur lors de l'enregistrement de l'historique", e);
        }
    }

    private String getClientIp() {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
