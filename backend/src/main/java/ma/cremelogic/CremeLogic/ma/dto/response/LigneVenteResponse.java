package ma.cremelogic.CremeLogic.ma.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class LigneVenteResponse {
    private Long id;
    private Long venteId;
    private Long produitId;
    private String produitNom;
    private String produitCode;
    private Integer quantite;
    private BigDecimal prixUnitaire;
    private BigDecimal remise;
    private BigDecimal montantTotal;
}