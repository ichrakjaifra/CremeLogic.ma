package ma.cremelogic.CremeLogic.ma.service.interfaces;

import ma.cremelogic.CremeLogic.ma.dto.request.IngredientRequest;
import ma.cremelogic.CremeLogic.ma.dto.response.IngredientResponse;
import java.math.BigDecimal;
import java.util.List;

public interface IngredientService {
    IngredientResponse createIngredient(IngredientRequest request);
    IngredientResponse updateIngredient(Long id, IngredientRequest request);
    IngredientResponse getIngredient(Long id);
    List<IngredientResponse> getAllIngredients();
    List<IngredientResponse> getIngredientsStockFaible();
    List<IngredientResponse> getIngredientsExpirant();
    List<IngredientResponse> searchIngredients(String keyword);
    void deleteIngredient(Long id);
    IngredientResponse ajusterStock(Long id, BigDecimal quantite, String type, String raison);
    BigDecimal getConsommationMoyenne(Long ingredientId, int jours);
    BigDecimal getValeurStockTotal();
    List<IngredientResponse> getIngredientsParFournisseur(Long fournisseurId);
    void verifierAlertesIngredient(Long ingredientId);
}
