package ma.cremelogic.CremeLogic.ma.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuiviEtapeResponse {
    private Long id;
    private Long etapeRecetteId;
    private String descriptionEtape;
    private Integer ordreEtape;
    private Integer tempsEstimeEtape;
    private String statut;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
}
