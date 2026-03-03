package ma.cremelogic.CremeLogic.ma.service.interfaces;

import ma.cremelogic.CremeLogic.ma.dto.request.ExecutionProductionRequest;
import ma.cremelogic.CremeLogic.ma.dto.request.OrdreProductionRequest;
import ma.cremelogic.CremeLogic.ma.dto.response.OrdreProductionResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface OrdreProductionService {
    // CRUD Operations
    OrdreProductionResponse createOrdre(OrdreProductionRequest request);
    OrdreProductionResponse updateOrdre(Long id, OrdreProductionRequest request);
    OrdreProductionResponse getOrdre(Long id);
    List<OrdreProductionResponse> getAllOrdres();
    List<OrdreProductionResponse> getOrdresByProduit(Long produitId);
    List<OrdreProductionResponse> getOrdresByStatut(String statut);
    void deleteOrdre(Long id);

    // Status Management
    OrdreProductionResponse changerStatut(Long id, String statut);
    OrdreProductionResponse demarrerProduction(Long id, ExecutionProductionRequest request);
    OrdreProductionResponse terminerProduction(Long id, ExecutionProductionRequest request);
    OrdreProductionResponse annulerProduction(Long id, String raison);

    // Business Logic
    List<OrdreProductionResponse> getOrdresEnRetard();
    BigDecimal getCoutTotalProductionPeriode(LocalDate debut, LocalDate fin);
    Integer getQuantiteProduite(Long produitId, LocalDate debut, LocalDate fin);

    // Utilities
    OrdreProductionResponse dupliquerOrdre(Long id);
    void consommerIngredients(Long ordreProductionId);
}