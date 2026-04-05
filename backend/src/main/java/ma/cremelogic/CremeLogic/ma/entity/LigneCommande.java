package ma.cremelogic.CremeLogic.ma.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "lignes_commande")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LigneCommande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "commande_id", nullable = false)
    private CommandeAchat commande;

    @ManyToOne
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal quantiteCommandee;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal quantiteRecue;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal prixUnitaire;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montantTotal;

    @PrePersist
    @PreUpdate
    protected void calculerMontant() {
        if (quantiteCommandee != null && prixUnitaire != null) {
            montantTotal = quantiteCommandee.multiply(prixUnitaire);
        }
        if (quantiteRecue == null) {
            quantiteRecue = BigDecimal.ZERO;
        }
    }
}
