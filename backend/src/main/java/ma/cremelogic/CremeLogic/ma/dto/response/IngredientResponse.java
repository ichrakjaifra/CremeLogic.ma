package ma.cremelogic.CremeLogic.ma.dto.response;

import ma.cremelogic.CremeLogic.ma.enums.UniteMesure;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class IngredientResponse {
    private Long id;
    private String codeIngredient;
    private String nom;
    private String description;
    private UniteMesure uniteMesure;
    private BigDecimal quantiteStock;
    private BigDecimal quantiteMinimum;
    private BigDecimal quantiteMaximum;
    private BigDecimal prixUnitaire;
    private boolean perissable;
    private LocalDate dateExpiration;
    private Long fournisseurPrincipalId;
    private String fournisseurNom;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;

    // Calculés
    private boolean stockFaible;
    private boolean enRupture;
    private boolean expire;
    private BigDecimal valeurStock;
    private BigDecimal consommationMoyenne;
}
