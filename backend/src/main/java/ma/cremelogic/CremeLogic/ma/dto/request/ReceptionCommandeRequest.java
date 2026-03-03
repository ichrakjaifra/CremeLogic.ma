package ma.cremelogic.CremeLogic.ma.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class ReceptionCommandeRequest {
    @NotNull(message = "La date de livraison réelle est obligatoire")
    private String dateLivraisonReelle;

    private List<LigneReceptionRequest> lignesRecues;
}