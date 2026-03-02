package ma.cremelogic.CremeLogic.ma.entity;

import ma.cremelogic.CremeLogic.ma.enums.ModePaiement;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ventes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String numeroVente;

    @Column(nullable = false)
    private LocalDateTime dateVente;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ModePaiement modePaiement;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montantTotal;

    @Column(precision = 10, scale = 2)
    private BigDecimal montantPaye;

    @Column(precision = 10, scale = 2)
    private BigDecimal montantRendu;

    private String nomClient;
    private String telephoneClient;
    private String emailClient;

    @Column(length = 1000)
    private String notes;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur caissier;

    @Builder.Default
    @OneToMany(mappedBy = "vente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LigneVente> lignesVente = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "vente", cascade = CascadeType.ALL)
    private List<MouvementStock> mouvements = new ArrayList<>();

    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
        dateModification = LocalDateTime.now();
        dateVente = LocalDateTime.now();
        if (numeroVente == null) {
            numeroVente = "VENTE-" + System.currentTimeMillis();
        }
        if (montantPaye == null) {
            montantPaye = BigDecimal.ZERO;
        }
        if (montantRendu == null) {
            montantRendu = BigDecimal.ZERO;
        }
        calculerMontantTotal();
    }

    @PreUpdate
    protected void onUpdate() {
        dateModification = LocalDateTime.now();
        calculerMontantTotal();
    }

    public void calculerMontantTotal() {
        if (lignesVente != null && !lignesVente.isEmpty()) {
            montantTotal = lignesVente.stream()
                    .map(LigneVente::getMontantTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } else {
            montantTotal = BigDecimal.ZERO;
        }
    }

    public BigDecimal getMontantDu() {
        return montantTotal.subtract(montantPaye);
    }

    public boolean estPayee() {
        return montantDu.compareTo(BigDecimal.ZERO) <= 0;
    }
}
