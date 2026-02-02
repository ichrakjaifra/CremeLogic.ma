package ma.cremelogic.CremeLogic.ma.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class HistoriqueActiviteResponse {
    private Long id;
    private String action;
    private String description;
    private String ipAddress;
    private LocalDateTime dateAction;
}