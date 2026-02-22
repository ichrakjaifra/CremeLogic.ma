package ma.cremelogic.CremeLogic.ma.repository;

import ma.cremelogic.CremeLogic.ma.entity.Ingredient;
import ma.cremelogic.CremeLogic.ma.enums.UniteMesure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    Optional<Ingredient> findByCodeIngredient(String codeIngredient);
    List<Ingredient> findByNomContainingIgnoreCase(String nom);
    List<Ingredient> findByUniteMesure(UniteMesure uniteMesure);
    List<Ingredient> findByPerissable(boolean perissable);

    @Query("SELECT i FROM Ingredient i WHERE i.quantiteStock <= i.quantiteMinimum")
    List<Ingredient> findIngredientsStockFaible();

    @Query("SELECT i FROM Ingredient i WHERE i.quantiteStock = 0")
    List<Ingredient> findIngredientsEnRupture();

    @Query("SELECT i FROM Ingredient i WHERE i.dateExpiration <= :date")
    List<Ingredient> findIngredientsExpirantAvant(LocalDate date);

    @Query("SELECT i FROM Ingredient i WHERE i.perissable = true AND i.dateExpiration IS NOT NULL AND i.dateExpiration <= CURRENT_DATE")
    List<Ingredient> findIngredientsExpires();

    @Query("SELECT i FROM Ingredient i ORDER BY i.quantiteStock ASC")
    List<Ingredient> findIngredientsByStockAsc();

    @Query("SELECT SUM(i.quantiteStock * i.prixUnitaire) FROM Ingredient i")
    BigDecimal getValeurStockTotal();

    boolean existsByCodeIngredient(String codeIngredient);
}