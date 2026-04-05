package ma.cremelogic.CremeLogic.ma.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class EvaluationResponse {
    private Long id;
    private Long fournisseurId;
    private String fournisseurNom;
    private Double note;
    private String commentaire;
    private Long evaluateurId;
    private String evaluateurNom;
    private LocalDateTime dateEvaluation;
}