package ma.cremelogic.CremeLogic.ma.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MouvementStockRequest {
    private Long ingredientId;
    private BigDecimal quantite;
    private BigDecimal coutUnitaire;
    private String raison;
    private Long commandeId;
    private Long ordreProductionId;
}
