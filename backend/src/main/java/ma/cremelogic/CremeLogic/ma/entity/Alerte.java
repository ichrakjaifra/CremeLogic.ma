package ma.cremelogic.CremeLogic.ma.entity;

import ma.cremelogic.CremeLogic.ma.enums.TypeAlerte;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "alertes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alerte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeAlerte type;

    @Column(nullable = false)
    private String titre;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private LocalDateTime dateCreation;

    private LocalDateTime dateResolution;

    @Column(nullable = false)
    private boolean resolue;

    @Column(nullable = false)
    private String priorite; // HAUTE, MOYENNE, BASSE

    @ManyToOne
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;

    @ManyToOne
    @JoinColumn(name = "produit_id")
    private Produit produit;

    @ManyToOne
    @JoinColumn(name = "commande_id")
    private CommandeAchat commande;

    @ManyToOne
    @JoinColumn(name = "ordre_production_id")
    private OrdreProduction ordreProduction;

    @SuppressWarnings("JpaAttributeTypeInspection")
    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    @ManyToOne
    @JoinColumn(name = "vente_id")
    private Vente vente;

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
        if (resolue == false) {
            resolue = false;
        }
    }

    public boolean estExpiree() {
        return !resolue && dateCreation.isBefore(LocalDateTime.now().minusDays(7));
    }
}
