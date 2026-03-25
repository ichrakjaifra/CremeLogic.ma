package ma.cremelogic.CremeLogic.ma.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "etapes_recette")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EtapeRecette {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Integer ordre; // Position in the recipe

    private Integer tempsEstime; // In minutes

    @ManyToOne
    @JoinColumn(name = "recette_id", nullable = false)
    private Recette recette;
}
