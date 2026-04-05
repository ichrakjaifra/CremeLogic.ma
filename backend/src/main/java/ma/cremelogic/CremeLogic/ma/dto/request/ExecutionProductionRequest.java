package ma.cremelogic.CremeLogic.ma.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class ExecutionProductionRequest {
    @NotNull(message = "La date de début réelle est obligatoire")
    private LocalDate dateDebutReelle;

    private String notes;
}