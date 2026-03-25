package ma.cremelogic.CremeLogic.ma.dto.response;

import ma.cremelogic.CremeLogic.ma.enums.StatutProduction;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrdreProductionResponse {
    private Long id;
    private String numeroOrdre;
    private Long produitId;
    private String produitNom;
    private Long recetteId;
    private Integer quantite;
    private LocalDate dateDebutPrevue;
    private LocalDate dateFinPrevue;
    private LocalDate dateDebutReelle;
    private LocalDate dateFinReelle;
    private StatutProduction statut;
    private BigDecimal coutTotal;
    private BigDecimal coutUnitaire;
    private String notes;
    private String instructionsRecette;
    private Long createurId;
    private String createurNom;
    private Long responsableId;
    private String responsableNom;
    private List<SuiviEtapeResponse> suivisEtapes;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
    private boolean enRetard;
}