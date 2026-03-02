package ma.cremelogic.CremeLogic.ma.dto.response;

import ma.cremelogic.CremeLogic.ma.enums.TypeAlerte;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AlerteResponse {
    private Long id;
    private TypeAlerte type;
    private String titre;
    private String description;
    private LocalDateTime dateCreation;
    private LocalDateTime dateResolution;
    private boolean resolue;
    private String priorite; // HAUTE, MOYENNE, BASSE

    // Références optionnelles
    private Long ingredientId;
    private String ingredientNom;

    private Long produitId;
    private String produitNom;

    private Long commandeId;
    private String commandeNumero;

    private Long ordreProductionId;
    private String ordreProductionNumero;

    private Long venteId;
    private String venteNumero;

    private Long utilisateurId;
    private String utilisateurNom;

    private String commentaireResolution;

    // Champs calculés
    private boolean estExpiree;
    private String age; // "Il y a X minutes/heures/jours"
}