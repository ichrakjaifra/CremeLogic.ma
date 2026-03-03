package ma.cremelogic.CremeLogic.ma.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class FournisseurResponse {
    private Long id;
    private String nom;
    private String telephone;
    private String email;
    private String adresse;
    private String ville;
    private String pays;
    private String codePostal;
    private String notes;
    private Double noteEvaluation;
    private boolean actif;
    private Integer nombreCommandes;
    private BigDecimal montantTotalCommandes;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
}