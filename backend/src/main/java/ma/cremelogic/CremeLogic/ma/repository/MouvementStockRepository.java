package ma.cremelogic.CremeLogic.ma.repository;

import ma.cremelogic.CremeLogic.ma.entity.MouvementStock;
import ma.cremelogic.CremeLogic.ma.enums.TypeMouvement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MouvementStockRepository extends JpaRepository<MouvementStock, Long> {
    List<MouvementStock> findByIngredientId(Long ingredientId);
    List<MouvementStock> findAllByOrderByDateMouvementDesc();

    List<MouvementStock> findByType(TypeMouvement type);

    List<MouvementStock> findByUtilisateurId(Long utilisateurId);

    List<MouvementStock> findByIngredientIdOrderByDateMouvementDesc(Long ingredientId);

    List<MouvementStock> findByDateMouvementBetweenOrderByDateMouvementDesc(LocalDateTime debut, LocalDateTime fin);

    List<MouvementStock> findByTypeOrderByDateMouvementDesc(TypeMouvement type);

    List<MouvementStock> findByIngredientIdAndDateMouvementBetweenOrderByDateMouvementDesc(Long ingredientId,
            LocalDateTime debut, LocalDateTime fin);

    @Query("SELECT m FROM MouvementStock m WHERE m.dateMouvement BETWEEN :startDate AND :endDate")
    List<MouvementStock> findByDateMouvementBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT m FROM MouvementStock m WHERE m.ingredient.id = :ingredientId AND m.dateMouvement BETWEEN :startDate AND :endDate ORDER BY m.dateMouvement DESC")
    List<MouvementStock> findMouvementsIngredientPeriode(Long ingredientId, LocalDateTime startDate,
            LocalDateTime endDate);

    @Query("SELECT SUM(m.quantite) FROM MouvementStock m WHERE m.ingredient.id = :ingredientId AND m.type = 'PERDU' AND m.dateMouvement BETWEEN :startDate AND :endDate")
    Double getTotalPertes(Long ingredientId, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT SUM(m.quantite) FROM MouvementStock m WHERE m.ingredient.id = :ingredientId AND m.type = 'ENTREE' AND m.dateMouvement BETWEEN :startDate AND :endDate")
    Double getTotalEntrees(Long ingredientId, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT SUM(m.quantite) FROM MouvementStock m WHERE m.ingredient.id = :ingredientId AND m.type = 'SORTIE' AND m.dateMouvement BETWEEN :startDate AND :endDate")
    Double getTotalSorties(Long ingredientId, LocalDateTime startDate, LocalDateTime endDate);
}
