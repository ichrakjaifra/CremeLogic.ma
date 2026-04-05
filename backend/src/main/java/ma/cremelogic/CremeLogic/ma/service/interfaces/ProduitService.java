package ma.cremelogic.CremeLogic.ma.service.interfaces;

import ma.cremelogic.CremeLogic.ma.dto.request.ProduitRequest;
import ma.cremelogic.CremeLogic.ma.dto.response.ProduitResponse;

import java.math.BigDecimal;
import java.util.List;

public interface ProduitService {
    ProduitResponse createProduit(ProduitRequest request);
    ProduitResponse updateProduit(Long id, ProduitRequest request);
    ProduitResponse getProduit(Long id);
    List<ProduitResponse> getAllProduits();
    List<ProduitResponse> getProduitsByCategorie(String categorie);
    List<ProduitResponse> getProduitsStockFaible();
    List<ProduitResponse> searchProduits(String keyword);
    void deleteProduit(Long id);
    ProduitResponse ajusterStock(Long id, Integer quantite, String type);
    ProduitResponse lierRecette(Long produitId, Long recetteId);
    void calculerCoutProduction(Long produitId);
    List<ProduitResponse> getProduitsPlusVendus(int limit);
    BigDecimal getValeurStockTotal();
}
