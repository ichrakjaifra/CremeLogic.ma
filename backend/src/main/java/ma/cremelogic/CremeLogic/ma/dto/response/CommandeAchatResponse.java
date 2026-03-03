package ma.cremelogic.CremeLogic.ma.dto.response;

import ma.cremelogic.CremeLogic.ma.enums.StatutCommande;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class CommandeAchatResponse {
    private Long id;
    private String numeroCommande;
    private Long fournisseurId;
    private String fournisseurNom;
    private LocalDate dateCommande;
    private LocalDate dateLivraisonPrevue;
    private LocalDate dateLivraisonReelle;
    private StatutCommande statut;
    private String notes;
    private BigDecimal montantTotal;
    private Long createurId;
    private String createurNom;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
    private List<LigneCommandeResponse> lignesCommande;
    private boolean enRetard;
}
