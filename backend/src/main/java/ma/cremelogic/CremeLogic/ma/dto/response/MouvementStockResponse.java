package ma.cremelogic.CremeLogic.ma.dto.response;

import ma.cremelogic.CremeLogic.ma.enums.TypeMouvement;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class MouvementStockResponse {
    private Long id;
    private LocalDateTime dateMouvement;

    // Ingrédient
    private Long ingredientId;
    private String ingredientNom;
    private String ingredientCode;

    // Type et quantités
    private TypeMouvement type;
    private BigDecimal quantite;
    private BigDecimal quantiteAvant;
    private BigDecimal quantiteApres;
    private String uniteMesure;

    // Informations financières
    private BigDecimal coutUnitaire;
    private BigDecimal montantTotal;

    // Utilisateur
    private Long utilisateurId;
    private String utilisateurNom;

    // Références
    private Long commandeId;
    private String commandeNumero;
    private Long ordreProductionId;
    private String ordreProductionNumero;
    private Long venteId;
    private String venteNumero;

    // Autres
    private String raison;
    private boolean synchronise;
}