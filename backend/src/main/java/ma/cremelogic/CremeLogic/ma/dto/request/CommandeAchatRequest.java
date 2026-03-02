package ma.cremelogic.CremeLogic.ma.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class CommandeAchatRequest {
    @NotNull(message = "Le fournisseur est obligatoire")
    private Long fournisseurId;

    private LocalDate dateLivraisonPrevue;

    private String notes;

    @NotNull(message = "Les lignes de commande sont obligatoires")
    @Size(min = 1, message = "Au moins une ligne de commande est requise")
    private List<LigneCommandeRequest> lignesCommande = new ArrayList<>();
}

@Data
class LigneCommandeRequest {
    @NotNull(message = "L'ingrédient est obligatoire")
    private Long ingredientId;

    @NotNull(message = "La quantité commandée est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "La quantité commandée doit être supérieure à 0")
    @Digits(integer = 10, fraction = 2, message = "La quantité commandée doit avoir au maximum 10 chiffres avant la virgule et 2 après")
    private BigDecimal quantiteCommandee;

    @NotNull(message = "Le prix unitaire est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix unitaire doit être supérieur à 0")
    @Digits(integer = 10, fraction = 2, message = "Le prix unitaire doit avoir au maximum 10 chiffres avant la virgule et 2 après")
    private BigDecimal prixUnitaire;
}
