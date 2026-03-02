package ma.cremelogic.CremeLogic.ma.repository;

import ma.cremelogic.CremeLogic.ma.dto.request.OrdreProductionRequest;
import ma.cremelogic.CremeLogic.ma.dto.response.OrdreProductionResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface OrdreProductionService {
    OrdreProductionResponse createOrdre(OrdreProductionRequest request);
    OrdreProductionResponse updateOrdre(Long id, OrdreProductionRequest request);
    OrdreProductionResponse getOrdre(Long id);
    List<OrdreProductionResponse> getAllOrdres();
    List<OrdreProductionResponse> getOrdresByProduit(Long produitId);
    List<OrdreProductionResponse> getOrdresByStatut(String statut);
    void deleteOrdre(Long id);
    OrdreProductionResponse changerStatut(Long id, String statut);
    OrdreProductionResponse demarrerProduction(Long id);
    OrdreProductionResponse terminerProduction(Long id);
    OrdreProductionResponse annulerProduction(Long id, String raison);
    List<OrdreProductionResponse> getOrdresEnRetard();
    BigDecimal getCoutTotalProductionPeriode(LocalDate debut, LocalDate fin);
    Integer getQuantiteProduite(Long produitId, LocalDate debut, LocalDate fin);
    OrdreProductionResponse dupliquerOrdre(Long id);
}
