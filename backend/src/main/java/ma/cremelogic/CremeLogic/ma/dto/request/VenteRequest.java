package ma.cremelogic.CremeLogic.ma.dto.request;

import ma.cremelogic.CremeLogic.ma.enums.ModePaiement;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class VenteRequest {
    @NotNull(message = "Le mode de paiement est obligatoire")
    private ModePaiement modePaiement;

    @NotNull(message = "Le montant payé est obligatoire")
    @DecimalMin(value = "0.0", message = "Le montant payé ne peut pas être négatif")
    @Digits(integer = 10, fraction = 2, message = "Le montant payé doit avoir au maximum 10 chiffres avant la virgule et 2 après")
    private BigDecimal montantPaye;

    private String nomClient;
    private String telephoneClient;
    private String emailClient;

    private String notes;

    @NotNull(message = "Les lignes de vente sont obligatoires")
    @Size(min = 1, message = "Au moins un produit est requis")
    private List<LigneVenteRequest> lignesVente = new ArrayList<>();
}

@Data
class LigneVenteRequest {
    @NotNull(message = "Le produit est obligatoire")
    private Long produitId;

    @NotNull(message = "La quantité est obligatoire")
    @Min(value = 1, message = "La quantité doit être au moins 1")
    private Integer quantite;

    private BigDecimal remise;
}
