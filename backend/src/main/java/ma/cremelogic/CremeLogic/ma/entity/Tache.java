package ma.cremelogic.CremeLogic.ma.entity;

import jakarta.persistence.*;
import lombok.*;
import ma.cremelogic.CremeLogic.ma.entity.Utilisateur;

import java.time.LocalDateTime;

@Entity
@Table(name = "taches")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tache {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private String statut; // A_FAIRE, EN_COURS, TERMINE, ANNULE

    @Column(nullable = false)
    private String priorite; // BASSE, MOYENNE, HAUTE

    private LocalDateTime dateEcheance;

    @SuppressWarnings("JpaAttributeTypeInspection")
    @ManyToOne
    @JoinColumn(name = "assigne_a_id")
    private Utilisateur assigneA;

    @SuppressWarnings("JpaAttributeTypeInspection")
    @ManyToOne
    @JoinColumn(name = "cree_par_id")
    private Utilisateur creePar;

    @Transient
    @com.fasterxml.jackson.annotation.JsonProperty("assigneAId")
    private Long transientAssigneAId;

    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
        dateModification = LocalDateTime.now();
        if (statut == null) statut = "A_FAIRE";
        if (priorite == null) priorite = "MOYENNE";
    }

    @PreUpdate
    protected void onUpdate() {
        dateModification = LocalDateTime.now();
    }
}
