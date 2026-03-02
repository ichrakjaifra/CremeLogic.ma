package ma.cremelogic.CremeLogic.ma.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AjustementStockRequest {

    @NotNull(message = "L'ID de l'ingrédient est obligatoire")
    private Long ingredientId;

    @NotNull(message = "La nouvelle quantité est obligatoire")
    @DecimalMin(value = "0.0", message = "La quantité ne peut pas être négative")
    @Digits(integer = 10, fraction = 2, message = "La quantité doit avoir au maximum 10 chiffres avant la virgule et 2 après")
    private BigDecimal nouvelleQuantite;

    @NotBlank(message = "La raison de l'ajustement est obligatoire")
    private String raison;

    private String typeAjustement; // "CORRECTION", "INVENTAIRE", "PERTE", "DON", etc.

    private String commentaire;

    private boolean forcerAjustement; // Pour forcer même si les alertes sont déclenchées
}