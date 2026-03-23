package ma.cremelogic.CremeLogic.ma.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class DashboardResponse {
    // Statistiques générales
    private Long totalProduits;
    private Long totalIngredients;
    private Long totalVentesJour;
    private Long totalCommandesJour;
    private Long totalProductionsJour;

    // Chiffres financiers
    private BigDecimal chiffreAffairesJour;
    private BigDecimal chiffreAffairesMois;
    private BigDecimal chiffreAffairesAnnee;
    private BigDecimal depensesJour;
    private BigDecimal depensesMois;
    private BigDecimal beneficeMois;

    // Stocks
    private Long produitsStockFaible;
    private Long ingredientsStockFaible;
    private Long ingredientsExpirant;
    private BigDecimal valeurStockTotal;

    // Alertes
    private Long alertesNonResolues;
    private Long alertesHautePriorite;

    // Graphiques
    private Map<String, BigDecimal> ventesParMois;
    private Map<String, BigDecimal> beneficesParMois;
    private Map<String, BigDecimal> ventesParCategorie;
    private Map<String, Long> produitsPlusVendus;

    // Fournisseurs
    private List<Map<String, Object>> topFournisseurs;

    // Activités récentes
    private List<VenteResponse> ventesRecent;
    private List<CommandeAchatResponse> commandesRecent;
    private List<OrdreProductionResponse> productionsRecent;
    private List<AlerteResponse> alertesRecent;
}
