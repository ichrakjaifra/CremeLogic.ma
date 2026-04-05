package ma.cremelogic.CremeLogic.ma.service.interfaces;

import ma.cremelogic.CremeLogic.ma.dto.response.DashboardResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface DashboardService {
    DashboardResponse getDashboardAdmin();
    DashboardResponse getDashboardChef();
    DashboardResponse getDashboardMagasinier();
    DashboardResponse getDashboardEmploye();

    // Statistiques spécifiques
    Map<String, BigDecimal> getVentesParMois(int mois);
    Map<String, BigDecimal> getVentesParCategorie(LocalDate debut, LocalDate fin);
    Map<String, Long> getProduitsPlusVendus(LocalDate debut, LocalDate fin, int limit);
    Map<String, BigDecimal> getCoutsParMois(int mois);
    Map<String, Long> getAlertesParType(LocalDate debut, LocalDate fin);

    // Prévisions
    Map<String, Object> getPrevisionsStock();
    Map<String, Object> getPrevisionsVentes();
    List<Map<String, Object>> getSuggestionsAchats();
}
