package ma.cremelogic.CremeLogic.ma.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "fournisseurs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Fournisseur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false, unique = true)
    private String telephone;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String adresse;

    private String ville;
    private String pays;
    private String codePostal;

    @Column(length = 1000)
    private String notes;

    @Column(precision = 3, scale = 2)
    private Double noteEvaluation; // 0-5

    private boolean actif;

    @Builder.Default
    @OneToMany(mappedBy = "fournisseur", cascade = CascadeType.ALL)
    private List<CommandeAchat> commandes = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "fournisseurPrincipal")
    private List<Ingredient> ingredients = new ArrayList<>();

    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
        dateModification = LocalDateTime.now();
        if (actif == false) {
            actif = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        dateModification = LocalDateTime.now();
    }

    public Integer getNombreCommandes() {
        return commandes != null ? commandes.size() : 0;
    }

    public BigDecimal getMontantTotalCommandes() {
        if (commandes == null || commandes.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return commandes.stream()
                .map(CommandeAchat::getMontantTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
