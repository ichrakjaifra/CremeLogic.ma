package ma.cremelogic.CremeLogic.ma.entity;

import ma.cremelogic.CremeLogic.ma.enums.UniteMesure;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ingredients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String codeIngredient;

    @Column(nullable = false)
    private String nom;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UniteMesure uniteMesure;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal quantiteStock;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal quantiteMinimum;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal quantiteMaximum;

    @Column(precision = 10, scale = 2)
    private BigDecimal prixUnitaire;

    @Column(nullable = false)
    private boolean perissable;

    private LocalDate dateExpiration;

    @ManyToOne
    @JoinColumn(name = "fournisseur_id")
    private Fournisseur fournisseurPrincipal;

    @Builder.Default
    @OneToMany(mappedBy = "ingredient", cascade = CascadeType.ALL)
    private List<LigneRecette> lignesRecette = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "ingredient", cascade = CascadeType.ALL)
    private List<LigneCommande> lignesCommande = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "ingredient", cascade = CascadeType.ALL)
    private List<MouvementStock> mouvements = new ArrayList<>();

    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
        dateModification = LocalDateTime.now();
        if (codeIngredient == null) {
            codeIngredient = "ING-" + System.currentTimeMillis();
        }
        if (quantiteStock == null) {
            quantiteStock = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        dateModification = LocalDateTime.now();
    }

    public boolean estStockFaible() {
        return quantiteStock.compareTo(quantiteMinimum) <= 0;
    }

    public boolean estEnRupture() {
        return quantiteStock.compareTo(BigDecimal.ZERO) <= 0;
    }

    public boolean estExpire() {
        return perissable && dateExpiration != null && dateExpiration.isBefore(LocalDate.now());
    }
}