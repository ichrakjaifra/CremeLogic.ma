package ma.cremelogic.CremeLogic.ma.service.interfaces;

import ma.cremelogic.CremeLogic.ma.dto.request.VenteRequest;
import ma.cremelogic.CremeLogic.ma.dto.response.ProduitResponse;
import ma.cremelogic.CremeLogic.ma.dto.response.VenteResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface VenteService {
    VenteResponse createVente(VenteRequest request);
    VenteResponse getVente(Long id);
    List<VenteResponse> getAllVentes();
    List<VenteResponse> getVentesByDate(LocalDateTime debut, LocalDateTime fin);
    List<VenteResponse> getVentesByCaissier(Long caissierId);
    List<VenteResponse> searchVentesByClient(String recherche);
    void annulerVente(Long id, String raison);
    VenteResponse genererFacture(Long id);
    BigDecimal getChiffreAffairesPeriode(LocalDateTime debut, LocalDateTime fin);
    Long getNombreVentesPeriode(LocalDateTime debut, LocalDateTime fin);
    List<VenteResponse> getVentesRecent(int limit);
    Map<String, BigDecimal> getVentesParCategorie(LocalDateTime debut, LocalDateTime fin);
    List<ProduitResponse> getProduitsPlusVendus(LocalDateTime debut, LocalDateTime fin, int limit);
}
