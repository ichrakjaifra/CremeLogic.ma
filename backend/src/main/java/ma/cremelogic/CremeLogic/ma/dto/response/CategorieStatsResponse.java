package ma.cremelogic.CremeLogic.ma.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CategorieStatsResponse {
    private String categorie;
    private Long nombreProduits;
    private Long nombreIngredients;
    private BigDecimal valeurStockProduits;
    private BigDecimal valeurStockIngredients;
    private BigDecimal chiffreAffairesMois;
    private BigDecimal ventesMois;
    private Long nombreVentes;
    private BigDecimal margeMoyenne;
}