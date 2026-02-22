package ma.cremelogic.CremeLogic.ma.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class RecetteResponse {
    private Long id;
    private String nom;
    private String description;
    private String instructions;
    private BigDecimal tempsPreparation;
    private BigDecimal tempsCuisson;
    private Integer nombrePortions;
    private BigDecimal coutTotal;
    private BigDecimal coutParPortion;
    private Long createurId;
    private String createurNom;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;

    private List<LigneRecetteResponse> lignesRecette;
}

@Data
@Builder
class LigneRecetteResponse {
    private Long id;
    private Long ingredientId;
    private String ingredientNom;
    private UniteMesure uniteMesure;
    private BigDecimal quantite;
    private String instructionsSpecifiques;
    private BigDecimal coutIngredient;
}
