package ma.cremelogic.CremeLogic.ma.repository;

import ma.cremelogic.CremeLogic.ma.entity.Recette;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface RecetteRepository extends JpaRepository<Recette, Long> {
    List<Recette> findByNomContainingIgnoreCase(String nom);
    List<Recette> findByCreateurId(Long createurId);

    @Query("SELECT r FROM Recette r WHERE r.coutTotal <= :maxCout")
    List<Recette> findByCoutTotalLessThanEqual(BigDecimal maxCout);

    @Query("SELECT r FROM Recette r WHERE r.nombrePortions >= :minPortions")
    List<Recette> findByNombrePortionsGreaterThanEqual(Integer minPortions);

    @Query("SELECT r FROM Recette r ORDER BY r.coutTotal DESC")
    List<Recette> findRecettesByCoutDesc();

    @Query("SELECT DISTINCT r FROM Recette r JOIN r.lignesRecette lr WHERE lr.ingredient.id = :ingredientId")
    List<Recette> findByIngredientId(Long ingredientId);
}
