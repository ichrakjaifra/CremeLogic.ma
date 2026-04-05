package ma.cremelogic.CremeLogic.ma.dto.response;

import ma.cremelogic.CremeLogic.ma.enums.ModePaiement;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class VenteResponse {
    private Long id;
    private String numeroVente;
    private LocalDateTime dateVente;
    private ModePaiement modePaiement;
    private BigDecimal montantTotal;
    private BigDecimal montantPaye;
    private BigDecimal montantRendu;
    private BigDecimal montantDu;
    private boolean estPayee;
    private String nomClient;
    private String telephoneClient;
    private String emailClient;
    private String notes;
    private Long caissierId;
    private String caissierNom;
    private LocalDateTime dateCreation;
    private List<LigneVenteResponse> lignesVente;
}