package ma.cremelogic.CremeLogic.ma.repository;

import ma.cremelogic.CremeLogic.ma.entity.Alerte;
import ma.cremelogic.CremeLogic.ma.enums.TypeAlerte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AlerteRepository extends JpaRepository<Alerte, Long> {
    List<Alerte> findByType(TypeAlerte type);

    List<Alerte> findByResolue(boolean resolue);

    List<Alerte> findByPriorite(String priorite);

    @Query("SELECT a FROM Alerte a WHERE a.dateCreation BETWEEN :startDate AND :endDate")
    List<Alerte> findByDateCreationBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT a FROM Alerte a WHERE a.resolue = false AND a.dateCreation < :date")
    List<Alerte> findAlertesNonResoluesExpirees(LocalDateTime date);

    @Query("SELECT COUNT(a) FROM Alerte a WHERE a.resolue = false AND a.priorite = 'HAUTE'")
    Long countAlertesHautePrioriteNonResolues();

    @Query("SELECT a FROM Alerte a WHERE a.resolue = false ORDER BY CASE a.priorite WHEN 'HAUTE' THEN 1 WHEN 'MOYENNE' THEN 2 WHEN 'BASSE' THEN 3 END, a.dateCreation DESC")
    List<Alerte> findAlertesNonResoluesTriees();

    Long countByResolueFalse();

    Long countByPrioriteAndResolueFalse(String priorite);

    void deleteByIngredientId(Long ingredientId);
}
