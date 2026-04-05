package ma.cremelogic.CremeLogic.ma.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

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

