package ma.cremelogic.CremeLogic.ma.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class StatsResponse {
    private Long totalProduits;
    private Long totalIngredients;
    private Long totalVentesJour;
    private Long totalCommandesJour;
    private Long totalProductionsJour;
    private Long totalFournisseurs;
    private Long totalUtilisateurs;

    private BigDecimal chiffreAffairesJour;
    private BigDecimal chiffreAffairesSemaine;
    private BigDecimal chiffreAffairesMois;
    private BigDecimal chiffreAffairesAnnee;

    private BigDecimal depensesJour;
    private BigDecimal depensesSemaine;
    private BigDecimal depensesMois;

    private BigDecimal beneficeJour;
    private BigDecimal beneficeSemaine;
    private BigDecimal beneficeMois;

    private Long produitsStockFaible;
    private Long ingredientsStockFaible;
    private Long ingredientsExpirant;

    private Long commandesEnAttente;
    private Long commandesEnRetard;
    private Long productionsEnCours;
    private Long productionsEnRetard;

    private Long alertesNonResolues;
}