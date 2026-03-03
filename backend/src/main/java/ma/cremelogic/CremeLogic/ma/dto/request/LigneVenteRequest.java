package ma.cremelogic.CremeLogic.ma.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class LigneVenteRequest {
    @NotNull(message = "Le produit est obligatoire")
    private Long produitId;

    @NotNull(message = "La quantité est obligatoire")
    @Min(value = 1, message = "La quantité doit être au moins 1")
    private Integer quantite;

    private BigDecimal remise;
}
