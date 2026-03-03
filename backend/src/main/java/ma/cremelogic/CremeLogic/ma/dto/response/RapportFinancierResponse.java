package ma.cremelogic.CremeLogic.ma.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class RapportFinancierResponse {
    private LocalDate dateDebut;
    private LocalDate dateFin;

    // Revenus
    private BigDecimal totalVentes;
    private Long nombreVentes;
    private Map<String, BigDecimal> ventesParCategorie;
    private Map<String, BigDecimal> ventesParJour;
    private List<ProduitVenteResponse> topProduits;

    // Dépenses
    private BigDecimal totalAchats;
    private Long nombreCommandes;
    private Map<String, BigDecimal> achatsParFournisseur;
    private BigDecimal coutTotalProduction;

    // Marges
    private BigDecimal margeBrute;
    private BigDecimal margePourcentage;
    private Map<String, BigDecimal> margesParCategorie;

    // Prévisions
    private BigDecimal previsionVentes;
    private BigDecimal previsionDepenses;
    private BigDecimal previsionMarge;
}