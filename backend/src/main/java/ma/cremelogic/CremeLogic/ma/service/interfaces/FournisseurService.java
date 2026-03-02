package ma.cremelogic.CremeLogic.ma.service.interfaces;

import ma.cremelogic.CremeLogic.ma.dto.request.FournisseurRequest;
import ma.cremelogic.CremeLogic.ma.dto.response.FournisseurResponse;

import java.math.BigDecimal;
import java.util.List;

public interface FournisseurService {
    FournisseurResponse createFournisseur(FournisseurRequest request);
    FournisseurResponse updateFournisseur(Long id, FournisseurRequest request);
    FournisseurResponse getFournisseur(Long id);
    List<FournisseurResponse> getAllFournisseurs();
    List<FournisseurResponse> getFournisseursActifs();
    List<FournisseurResponse> searchFournisseurs(String keyword);
    void deleteFournisseur(Long id);
    FournisseurResponse evaluerFournisseur(Long id, Double note, String commentaire);
    List<FournisseurResponse> getFournisseursParVille(String ville);
    BigDecimal getMontantTotalCommandes(Long fournisseurId);
    Integer getNombreCommandes(Long fournisseurId);
    FournisseurResponse toggleActif(Long id);
}
