package ma.cremelogic.CremeLogic.ma.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class LigneRecetteRequest {
    @NotNull(message = "L'ingrédient est obligatoire")
    private Long ingredientId;

    @NotNull(message = "La quantité est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "La quantité doit être supérieure à 0")
    @Digits(integer = 10, fraction = 4, message = "La quantité doit avoir au maximum 10 chiffres avant la virgule et 4 après")
    private BigDecimal quantite;

    private String instructionsSpecifiques;
}