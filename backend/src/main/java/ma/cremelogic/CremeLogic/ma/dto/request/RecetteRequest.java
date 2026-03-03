package ma.cremelogic.CremeLogic.ma.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class RecetteRequest {
    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 200, message = "Le nom doit contenir entre 2 et 200 caractères")
    private String nom;

    private String description;

    @Size(max = 4000, message = "Les instructions ne peuvent pas dépasser 4000 caractères")
    private String instructions;

    @DecimalMin(value = "0.0", message = "Le temps de préparation ne peut pas être négatif")
    private BigDecimal tempsPreparation;

    @DecimalMin(value = "0.0", message = "Le temps de cuisson ne peut pas être négatif")
    private BigDecimal tempsCuisson;

    @NotNull(message = "Le nombre de portions est obligatoire")
    @Min(value = 1, message = "Le nombre de portions doit être au moins 1")
    private Integer nombrePortions;

    @NotNull(message = "Les lignes de recette sont obligatoires")
    @Size(min = 1, message = "Au moins un ingrédient est requis")
    private List<LigneRecetteRequest> lignesRecette = new ArrayList<>();
}