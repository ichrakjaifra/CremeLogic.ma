package ma.cremelogic.CremeLogic.ma.dto.request;

import ma.cremelogic.CremeLogic.ma.enums.UniteMesure;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class IngredientRequest {
    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
    private String nom;

    private String description;

    @NotNull(message = "L'unité de mesure est obligatoire")
    private UniteMesure uniteMesure;

    @NotNull(message = "La quantité minimum est obligatoire")
    @DecimalMin(value = "0.0", message = "La quantité minimum ne peut pas être négative")
    @Digits(integer = 10, fraction = 2, message = "La quantité minimum doit avoir au maximum 10 chiffres avant la virgule et 2 après")
    private BigDecimal quantiteMinimum;

    @NotNull(message = "La quantité maximum est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "La quantité maximum doit être supérieure à 0")
    @Digits(integer = 10, fraction = 2, message = "La quantité maximum doit avoir au maximum 10 chiffres avant la virgule et 2 après")
    private BigDecimal quantiteMaximum;

    @DecimalMin(value = "0.0", message = "Le prix unitaire ne peut pas être négatif")
    @Digits(integer = 10, fraction = 2, message = "Le prix unitaire doit avoir au maximum 10 chiffres avant la virgule et 2 après")
    private BigDecimal prixUnitaire;

    @NotNull(message = "Le champ périssable est obligatoire")
    private boolean perissable;

    private LocalDate dateExpiration;
    private Long fournisseurPrincipalId;
}