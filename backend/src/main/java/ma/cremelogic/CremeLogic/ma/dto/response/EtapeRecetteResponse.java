package ma.cremelogic.CremeLogic.ma.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EtapeRecetteResponse {
    private Long id;
    private String description;
    private Integer ordre;
    private Integer tempsEstime;
}
