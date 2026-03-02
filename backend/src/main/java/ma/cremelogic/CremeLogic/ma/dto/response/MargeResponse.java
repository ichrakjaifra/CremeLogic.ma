package ma.cremelogic.CremeLogic.ma.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class MargeResponse {
    private Long produitId;
    private String produitNom;
    private BigDecimal prixVente;
    private BigDecimal coutProduction;
    private BigDecimal margeBrute;
    private BigDecimal margePourcentage;
    private BigDecimal margeTotale;
    private Integer quantiteVendue;
}