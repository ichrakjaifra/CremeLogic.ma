package ma.cremelogic.CremeLogic.ma.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProduitVenteResponse {
    private Long produitId;
    private String produitNom;
    private String produitCode;
    private String categorie;
    private Integer quantiteVendue;
    private BigDecimal montantTotal;
    private BigDecimal prixMoyen;
    private Integer nombreVentes;
    private BigDecimal partPourcentage; // Pourcentage du chiffre d'affaires total
}