package ma.cremelogic.CremeLogic.ma.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "lignes_vente")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LigneVente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vente_id", nullable = false)
    private Vente vente;

    @ManyToOne
    @JoinColumn(name = "produit_id", nullable = false)
    private Produit produit;

    @Column(nullable = false)
    private Integer quantite;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal prixUnitaire;

    @Column(precision = 10, scale = 2)
    private BigDecimal remise;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montantTotal;

    @PrePersist
    @PreUpdate
    protected void calculerMontant() {
        if (quantite != null && prixUnitaire != null) {
            BigDecimal total = prixUnitaire.multiply(new BigDecimal(quantite));
            if (remise != null && remise.compareTo(BigDecimal.ZERO) > 0) {
                total = total.subtract(remise);
            }
            montantTotal = total;
        }
    }
}
