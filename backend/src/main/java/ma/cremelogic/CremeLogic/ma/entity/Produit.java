package ma.cremelogic.CremeLogic.ma.entity;

import ma.cremelogic.CremeLogic.ma.enums.StatutProduit;
import ma.cremelogic.CremeLogic.ma.enums.CategorieProduit;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "produits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Produit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String codeProduit;

    @Column(nullable = false)
    private String nom;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategorieProduit categorie;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal prixVente;

    @Column(precision = 10, scale = 2)
    private BigDecimal coutProduction;

    @Column(precision = 5, scale = 2)
    private BigDecimal marge;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutProduit statut;

    @Column(nullable = false)
    private Integer stockDisponible;

    @Column(nullable = false)
    private Integer stockMinimum;

    @Column(nullable = false)
    private Integer stockMaximum;

    @OneToOne
    @JoinColumn(name = "recette_id")
    private Recette recette;

    private String imageUrl;

    @Builder.Default
    @OneToMany(mappedBy = "produit", cascade = CascadeType.ALL)
    private List<LigneVente> lignesVente = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "produit", cascade = CascadeType.ALL)
    private List<OrdreProduction> ordresProduction = new ArrayList<>();

    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
        dateModification = LocalDateTime.now();
        if (codeProduit == null) {
            codeProduit = "PROD-" + System.currentTimeMillis();
        }
        if (stockDisponible == null) {
            stockDisponible = 0;
        }
        calculerMarge();
    }

    @PreUpdate
    protected void onUpdate() {
        dateModification = LocalDateTime.now();
        calculerMarge();
    }

    private void calculerMarge() {
        if (prixVente != null && coutProduction != null && coutProduction.compareTo(BigDecimal.ZERO) > 0) {
            marge = prixVente.subtract(coutProduction)
                    .divide(coutProduction, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(new BigDecimal("100"));
        }
    }
}