package ma.cremelogic.CremeLogic.ma.entity;

import ma.cremelogic.CremeLogic.ma.enums.TypeMouvement;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "mouvements_stock")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MouvementStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime dateMouvement;

    @ManyToOne
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeMouvement type;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal quantite;

    @Column(precision = 10, scale = 2)
    private BigDecimal quantiteAvant;

    @Column(precision = 10, scale = 2)
    private BigDecimal quantiteApres;

    @Column(precision = 10, scale = 2)
    private BigDecimal coutUnitaire;

    @Column(precision = 10, scale = 2)
    private BigDecimal montantTotal;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    @ManyToOne
    @JoinColumn(name = "commande_id")
    private CommandeAchat commande;

    @ManyToOne
    @JoinColumn(name = "ordre_production_id")
    private OrdreProduction ordreProduction;

    @ManyToOne
    @JoinColumn(name = "vente_id")
    private Vente vente;

    @Column(length = 1000)
    private String raison;

    @Column(nullable = false)
    private boolean synchronise;

    @PrePersist
    protected void onCreate() {
        dateMouvement = LocalDateTime.now();
        if (quantiteAvant == null) {
            quantiteAvant = ingredient.getQuantiteStock();
        }
        if (quantiteApres == null) {
            quantiteApres = calculerQuantiteApres();
        }
        if (montantTotal == null && coutUnitaire != null && quantite != null) {
            montantTotal = coutUnitaire.multiply(quantite);
        }
        synchronise = false;
    }

    private BigDecimal calculerQuantiteApres() {
        BigDecimal quantiteApres = quantiteAvant;
        switch (type) {
            case ENTREE, AJUSTEMENT -> quantiteApres = quantiteAvant.add(quantite);
            case SORTIE, PERDU, DETRUIT -> quantiteApres = quantiteAvant.subtract(quantite);
        }
        return quantiteApres;
    }
}
