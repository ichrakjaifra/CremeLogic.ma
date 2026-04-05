package ma.cremelogic.CremeLogic.ma.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class FournisseurRequest {
    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
    private String nom;

    @NotBlank(message = "Le téléphone est obligatoire")
    @Pattern(regexp = "^[0-9\\+\\-\\(\\)\\s]{8,20}$", message = "Format de téléphone invalide")
    private String telephone;

    @Email(message = "Format d'email invalide")
    private String email;

    @NotBlank(message = "L'adresse est obligatoire")
    @Size(min = 5, max = 200, message = "L'adresse doit contenir entre 5 et 200 caractères")
    private String adresse;

    private String ville;
    private String pays;
    private String codePostal;

    private String notes;
    private Double noteEvaluation;
    private boolean actif;
}
