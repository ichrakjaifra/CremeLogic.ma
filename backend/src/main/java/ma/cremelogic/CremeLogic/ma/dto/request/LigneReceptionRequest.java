package ma.cremelogic.CremeLogic.ma.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class LigneReceptionRequest {
    @NotNull(message = "L'ID de la ligne de commande est obligatoire")
    private Long ligneCommandeId;

    @NotNull(message = "La quantité reçue est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "La quantité reçue doit être supérieure à 0")
    private BigDecimal quantiteRecue;
}