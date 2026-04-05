package ma.cremelogic.CremeLogic.ma.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class LigneCommandeResponse {
    private Long id;
    private Long commandeId;
    private Long ingredientId;
    private String ingredientNom;
    private String ingredientCode;
    private BigDecimal quantiteCommandee;
    private BigDecimal quantiteRecue;
    private BigDecimal prixUnitaire;
    private BigDecimal montantTotal;
    private String uniteMesure;
}