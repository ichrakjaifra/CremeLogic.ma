package ma.cremelogic.CremeLogic.ma.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TacheResponse {
    private Long id;
    private String titre;
    private String description;
    private String statut;
    private String priorite;
    private LocalDateTime dateEcheance;
    private String assigneANom;
    private LocalDateTime dateCreation;
}
