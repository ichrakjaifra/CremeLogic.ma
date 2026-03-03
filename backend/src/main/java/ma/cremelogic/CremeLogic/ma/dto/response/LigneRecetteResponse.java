package ma.cremelogic.CremeLogic.ma.dto.response;

import lombok.Builder;
import lombok.Data;
import ma.cremelogic.CremeLogic.ma.enums.UniteMesure;

import java.math.BigDecimal;

@Data
@Builder
public class LigneRecetteResponse {
    private Long id;
    private Long ingredientId;
    private String ingredientNom;
    private UniteMesure uniteMesure;
    private BigDecimal quantite;
    private String instructionsSpecifiques;
    private BigDecimal coutIngredient;
}
