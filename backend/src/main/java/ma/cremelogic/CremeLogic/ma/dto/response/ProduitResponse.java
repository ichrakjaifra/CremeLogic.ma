package ma.cremelogic.CremeLogic.ma.dto.response;

import ma.cremelogic.CremeLogic.ma.enums.CategorieProduit;
import ma.cremelogic.CremeLogic.ma.enums.StatutProduit;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ProduitResponse {
    private Long id;
    private String codeProduit;
    private String nom;
    private String description;
    private CategorieProduit categorie;
    private BigDecimal prixVente;
    private BigDecimal coutProduction;
    private BigDecimal marge;
    private StatutProduit statut;
    private Integer stockDisponible;
    private Integer stockMinimum;
    private Integer stockMaximum;
    private Long recetteId;
    private String imageUrl;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;

    // Calculés
    private boolean stockFaible;
    private boolean enRupture;
    private BigDecimal valeurStock;
}
