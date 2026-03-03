package ma.cremelogic.CremeLogic.ma.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EvaluationRequest {
    @NotNull(message = "La note est obligatoire")
    @Min(value = 0, message = "La note doit être au moins 0")
    @Max(value = 5, message = "La note ne peut pas dépasser 5")
    private Double note;

    private String commentaire;
}