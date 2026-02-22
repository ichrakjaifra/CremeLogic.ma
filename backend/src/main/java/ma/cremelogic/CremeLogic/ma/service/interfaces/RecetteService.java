package ma.cremelogic.CremeLogic.ma.service.interfaces;

import ma.cremelogic.CremeLogic.ma.dto.request.RecetteRequest;
import ma.cremelogic.CremeLogic.ma.dto.response.RecetteResponse;

import java.math.BigDecimal;
import java.util.List;

public interface RecetteService {
    RecetteResponse createRecette(RecetteRequest request);
    RecetteResponse updateRecette(Long id, RecetteRequest request);
    RecetteResponse getRecette(Long id);
    List<RecetteResponse> getAllRecettes();
    List<RecetteResponse> getRecettesByCreateur(Long createurId);
    List<RecetteResponse> searchRecettes(String keyword);
    void deleteRecette(Long id);
    RecetteResponse dupliquerRecette(Long id, String nouveauNom);
    BigDecimal calculerCoutRecette(Long recetteId);
    List<RecetteResponse> getRecettesByIngredient(Long ingredientId);
    RecetteResponse ajusterPortions(Long recetteId, Integer nouvellesPortions);
}
