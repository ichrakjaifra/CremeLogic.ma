package ma.cremelogic.CremeLogic.ma.repository;

import ma.cremelogic.CremeLogic.ma.entity.Vente;
import ma.cremelogic.CremeLogic.ma.enums.ModePaiement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VenteRepository extends JpaRepository<Vente, Long> {
    List<Vente> findAllByOrderByDateVenteDesc();

    List<Vente> findByDateVenteBetweenOrderByDateVenteDesc(LocalDateTime debut, LocalDateTime fin);

    List<Vente> findByCaissierIdOrderByDateVenteDesc(Long caissierId);

    List<Vente> findByNomClientContainingIgnoreCaseOrTelephoneClientContainingIgnoreCase(String nom, String telephone);

    @Query("SELECT v FROM Vente v WHERE v.dateVente BETWEEN :startDate AND :endDate")
    List<Vente> findByDateVenteBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT SUM(v.montantTotal) FROM Vente v WHERE v.dateVente BETWEEN :startDate AND :endDate")
    Double getChiffreAffairesPeriode(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT COUNT(v) FROM Vente v WHERE v.dateVente BETWEEN :startDate AND :endDate")
    Long getNombreVentesPeriode(LocalDateTime startDate, LocalDateTime endDate);

    Long countByCaissierIdAndDateVenteBetween(Long caissierId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT SUM(v.montantTotal) FROM Vente v WHERE v.caissier.id = :caissierId AND v.dateVente BETWEEN :startDate AND :endDate")
    java.math.BigDecimal sumMontantTotalByCaissierIdAndDateVenteBetween(Long caissierId, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT v FROM Vente v ORDER BY v.dateVente DESC")
    List<Vente> findRecentVentes(int limit);
}
