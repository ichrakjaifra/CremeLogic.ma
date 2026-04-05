package ma.cremelogic.CremeLogic.ma.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import ma.cremelogic.CremeLogic.ma.enums.Role;

@Data
public class UpdateUtilisateurRequest {

    @NotBlank(message = "Le nom est obligatoire")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s\\-]*$", message = "Le nom ne doit contenir que des lettres")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s\\-]*$", message = "Le prénom ne doit contenir que des lettres")
    private String prenom;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    private String email;

    @Pattern(regexp = "^$|^0[5-7][0-9]{8}$",
            message = "Numéro de téléphone marocain invalide (ex: 0612345678)")
    private String telephone;

    private Role role;
    
    private Boolean actif;
}