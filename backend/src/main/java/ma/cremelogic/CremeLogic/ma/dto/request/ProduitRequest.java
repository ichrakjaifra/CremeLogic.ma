package ma.cremelogic.CremeLogic.ma.dto.request;


import ma.cremelogic.CremeLogic.ma.enums.CategorieProduit;
import ma.cremelogic.CremeLogic.ma.enums.StatutProduit;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProduitRequest {
    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
    private String nom;

    private String description;

    @NotNull(message = "La catégorie est obligatoire")
    private CategorieProduit categorie;

    @NotNull(message = "Le prix de vente est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix de vente doit être supérieur à 0")
    @Digits(integer = 10, fraction = 2, message = "Le prix de vente doit avoir au maximum 10 chiffres avant la virgule et 2 après")
    private BigDecimal prixVente;

    @NotNull(message = "Le stock minimum est obligatoire")
    @Min(value = 0, message = "Le stock minimum ne peut pas être négatif")
    private Integer stockMinimum;

    @NotNull(message = "Le stock maximum est obligatoire")
    @Min(value = 1, message = "Le stock maximum doit être au moins 1")
    private Integer stockMaximum;

    private Long recetteId;
    private String imageUrl;
    private StatutProduit statut;
}
