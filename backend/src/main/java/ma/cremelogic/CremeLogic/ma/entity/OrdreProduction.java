package ma.cremelogic.CremeLogic.ma.entity;

import ma.cremelogic.CremeLogic.ma.enums.StatutProduction;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ordres_production")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdreProduction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String numeroOrdre;

    @ManyToOne
    @JoinColumn(name = "produit_id", nullable = false)
    private Produit produit;

    @Column(nullable = false)
    private Integer quantite;

    @Column(nullable = false)
    private LocalDate dateDebutPrevue;

    private LocalDate dateFinPrevue;
    private LocalDate dateDebutReelle;
    private LocalDate dateFinReelle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutProduction statut;

    @Column(precision = 10, scale = 2)
    private BigDecimal coutTotal;

    @Column(precision = 10, scale = 2)
    private BigDecimal coutUnitaire;

    @Column(length = 1000)
    private String notes;

    @SuppressWarnings("JpaAttributeTypeInspection")
    @ManyToOne
    @JoinColumn(name = "createur_id")
    private Utilisateur createur;

    @SuppressWarnings("JpaAttributeTypeInspection")
    @ManyToOne
    @JoinColumn(name = "responsable_id")
    private Utilisateur responsable;

    @Builder.Default
    @OneToMany(mappedBy = "ordreProduction", cascade = CascadeType.ALL)
    private List<MouvementStock> mouvements = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "ordreProduction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SuiviEtape> suivisEtapes = new ArrayList<>();

    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
        dateModification = LocalDateTime.now();
        if (numeroOrdre == null) {
            numeroOrdre = "ORD-" + System.currentTimeMillis();
        }
        if (statut == null) {
            statut = StatutProduction.PLANIFIEE;
        }
        calculerCouts();
    }

    @PreUpdate
    protected void onUpdate() {
        dateModification = LocalDateTime.now();
        calculerCouts();
    }

    public void calculerCouts() {
        if (produit != null && produit.getCoutProduction() != null && quantite != null) {
            coutUnitaire = produit.getCoutProduction();
            coutTotal = coutUnitaire.multiply(new BigDecimal(quantite));
        }
    }

    public boolean estEnRetard() {
        return dateFinPrevue != null &&
                dateFinPrevue.isBefore(LocalDate.now()) &&
                statut != StatutProduction.TERMINEE;
    }
}
