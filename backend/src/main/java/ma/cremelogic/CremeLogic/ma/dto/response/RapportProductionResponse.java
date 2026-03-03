package ma.cremelogic.CremeLogic.ma.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class RapportProductionResponse {
    private LocalDate dateDebut;
    private LocalDate dateFin;

    private Long totalOrdres;
    private Long ordresTermines;
    private Long ordresEnCours;
    private Long ordresEnRetard;

    private Integer totalProduitsFabriques;
    private BigDecimal valeurProduction;

    private Map<String, Long> productionParProduit;
    private Map<String, BigDecimal> coutsParProduit;

    private BigDecimal efficaciteMoyenne;
    private List<String> suggestionsOptimisation;

    // Consommation
    private Map<String, BigDecimal> consommationIngredients;
    private Map<String, BigDecimal> pertesIngredients;
}