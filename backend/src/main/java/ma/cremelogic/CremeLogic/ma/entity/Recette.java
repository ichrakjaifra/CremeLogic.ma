package ma.cremelogic.CremeLogic.ma.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "recettes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recette {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(length = 2000)
    private String description;

    @Column(length = 4000)
    private String instructions;

    @Column(precision = 5, scale = 2)
    private BigDecimal tempsPreparation; // en minutes

    @Column(precision = 5, scale = 2)
    private BigDecimal tempsCuisson; // en minutes

    @Column(nullable = false)
    private Integer nombrePortions;

    @Column(precision = 10, scale = 2)
    private BigDecimal coutTotal;

    @OneToOne(mappedBy = "recette")
    private Produit produit;

    @Builder.Default
    @OneToMany(mappedBy = "recette", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LigneRecette> lignesRecette = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "recette", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ordre ASC")
    private List<EtapeRecette> etapes = new ArrayList<>();

    @SuppressWarnings("JpaAttributeTypeInspection")
    @ManyToOne
    @JoinColumn(name = "createur_id")
    private Utilisateur createur;

    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
        dateModification = LocalDateTime.now();
        calculerCoutTotal();
    }

    @PreUpdate
    protected void onUpdate() {
        dateModification = LocalDateTime.now();
        calculerCoutTotal();
    }

    public void calculerCoutTotal() {
        if (lignesRecette != null && !lignesRecette.isEmpty()) {
            coutTotal = lignesRecette.stream()
                    .map(ligne -> {
                        BigDecimal prixIngredient = ligne.getIngredient().getPrixUnitaire();
                        BigDecimal quantite = ligne.getQuantite();
                        if (prixIngredient != null && quantite != null) {
                            return prixIngredient.multiply(quantite);
                        }
                        return BigDecimal.ZERO;
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } else {
            coutTotal = BigDecimal.ZERO;
        }
    }

    public BigDecimal getCoutParPortion() {
        if (coutTotal != null && nombrePortions > 0) {
            return coutTotal.divide(new BigDecimal(nombrePortions), 2, BigDecimal.ROUND_HALF_UP);
        }
        return BigDecimal.ZERO;
    }
}
