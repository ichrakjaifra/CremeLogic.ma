package ma.cremelogic.CremeLogic.ma.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class OrdreProductionRequest {
    @NotNull(message = "Le produit est obligatoire")
    private Long produitId;

    @NotNull(message = "La quantité est obligatoire")
    @Min(value = 1, message = "La quantité doit être au moins 1")
    private Integer quantite;

    @NotNull(message = "La date de début prévue est obligatoire")
    @FutureOrPresent(message = "La date de début doit être aujourd'hui ou dans le futur")
    private LocalDate dateDebutPrevue;

    private LocalDate dateFinPrevue;

    private String notes;

    private Long responsableId;
}