package ma.cremelogic.CremeLogic.ma.service.interfaces;

import ma.cremelogic.CremeLogic.ma.dto.response.MouvementStockResponse;
import ma.cremelogic.CremeLogic.ma.enums.TypeMouvement;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface MouvementStockService {
    MouvementStockResponse enregistrerEntree(Long ingredientId, BigDecimal quantite, BigDecimal coutUnitaire, String raison, Long commandeId);
    MouvementStockResponse enregistrerSortie(Long ingredientId, BigDecimal quantite, String raison, Long ordreProductionId);
    MouvementStockResponse enregistrerPerte(Long ingredientId, BigDecimal quantite, String raison);
    MouvementStockResponse enregistrerAjustement(Long ingredientId, BigDecimal nouvelleQuantite, String raison);

    List<MouvementStockResponse> getMouvementsParIngredient(Long ingredientId);
    List<MouvementStockResponse> getMouvementsParPeriode(LocalDateTime debut, LocalDateTime fin);
    List<MouvementStockResponse> getMouvementsParType(TypeMouvement type);

    Map<String, BigDecimal> getStatistiquesConsommation(Long ingredientId, LocalDateTime debut, LocalDateTime fin);
    Map<String, Object> getHistoriqueComplet(Long ingredientId, LocalDateTime debut, LocalDateTime fin);

    void verifierEtCreerAlertes();
    void deduireConsommationProduction(Long ordreProductionId, Map<Long, BigDecimal> consommations);
}