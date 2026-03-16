package ma.cremelogic.CremeLogic.ma.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "historique_activite")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoriqueActivite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @SuppressWarnings("JpaAttributeTypeInspection")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    @Column(nullable = false)
    private String action; // CREATE, UPDATE, DELETE, LOGIN, etc.

    private String entite;
    private Long entiteId;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(length = 1000)
    private String ancienneValeur;

    @Column(length = 1000)
    private String nouvelleValeur;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime dateAction = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        if (dateAction == null) {
            dateAction = LocalDateTime.now();
        }
    }
}