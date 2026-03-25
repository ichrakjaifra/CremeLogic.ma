package ma.cremelogic.CremeLogic.ma.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "suivis_etape")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SuiviEtape {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ordre_production_id", nullable = false)
    private OrdreProduction ordreProduction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etape_recette_id", nullable = false)
    private EtapeRecette etapeRecette;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private StatutSuivi statut = StatutSuivi.A_FAIRE;

    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;

    // Inner enum for simplicity within the entity file if not reused elsewhere
    public enum StatutSuivi {
        A_FAIRE, EN_COURS, TERMINEE
    }
}
