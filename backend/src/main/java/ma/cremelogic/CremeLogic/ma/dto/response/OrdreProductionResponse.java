package ma.cremelogic.CremeLogic.ma.dto.response;

import ma.cremelogic.CremeLogic.ma.enums.StatutProduction;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class OrdreProductionResponse {
    private Long id;
    private String numeroOrdre;
    private Long produitId;
    private String produitNom;
    private Integer quantite;
    private LocalDate dateDebutPrevue;
    private LocalDate dateFinPrevue;
    private LocalDate dateDebutReelle;
    private LocalDate dateFinReelle;
    private StatutProduction statut;
    private BigDecimal coutTotal;
    private BigDecimal coutUnitaire;
    private String notes;
    private Long createurId;
    private String createurNom;
    private Long responsableId;
    private String responsableNom;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
    private boolean enRetard;
}