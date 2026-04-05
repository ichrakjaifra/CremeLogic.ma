package ma.cremelogic.CremeLogic.ma.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ma.cremelogic.CremeLogic.ma.enums.Role;

@Data
public class CreateUtilisateurRequest {

    @NotBlank(message = "Le nom est obligatoire")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s\\-]*$", message = "Le nom ne doit contenir que des lettres")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s\\-]*$", message = "Le prénom ne doit contenir que des lettres")
    private String prenom;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    private String password;

    @Pattern(regexp = "^$|^0[5-7][0-9]{8}$",
            message = "Numéro de téléphone marocain invalide (ex: 0612345678)")
    private String telephone;

    @NotNull(message = "Le rôle est obligatoire")
    private Role role;
}
