package ma.cremelogic.CremeLogic.ma.entity;

import ma.cremelogic.CremeLogic.ma.enums.StatutCommande;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "commandes_achat")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommandeAchat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String numeroCommande;

    @ManyToOne
    @JoinColumn(name = "fournisseur_id", nullable = false)
    private Fournisseur fournisseur;

    @Column(nullable = false)
    private LocalDate dateCommande;

    private LocalDate dateLivraisonPrevue;
    private LocalDate dateLivraisonReelle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutCommande statut;

    @Column(length = 1000)
    private String notes;

    @Column(precision = 10, scale = 2)
    private BigDecimal montantTotal;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur createur;

    @Builder.Default
    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LigneCommande> lignesCommande = new ArrayList<>();

    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
        dateModification = LocalDateTime.now();
        dateCommande = LocalDate.now();
        if (numeroCommande == null) {
            numeroCommande = "CMD-" + System.currentTimeMillis();
        }
        if (statut == null) {
            statut = StatutCommande.EN_ATTENTE;
        }
        calculerMontantTotal();
    }

    @PreUpdate
    protected void onUpdate() {
        dateModification = LocalDateTime.now();
        calculerMontantTotal();
    }

    public void calculerMontantTotal() {
        if (lignesCommande != null && !lignesCommande.isEmpty()) {
            montantTotal = lignesCommande.stream()
                    .map(LigneCommande::getMontantTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } else {
            montantTotal = BigDecimal.ZERO;
        }
    }

    public boolean estEnRetard() {
        return dateLivraisonPrevue != null &&
                dateLivraisonPrevue.isBefore(LocalDate.now()) &&
                statut != StatutCommande.LIVREE;
    }
}