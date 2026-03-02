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
    Optional<Vente> findByNumeroVente(String numeroVente);
    List<Vente> findByCaissierId(Long caissierId);
    List<Vente> findByModePaiement(ModePaiement modePaiement);

    @Query("SELECT v FROM Vente v WHERE v.dateVente BETWEEN :startDate AND :endDate")
    List<Vente> findByDateVenteBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT SUM(v.montantTotal) FROM Vente v WHERE v.dateVente BETWEEN :startDate AND :endDate")
    Double getChiffreAffairesPeriode(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT COUNT(v) FROM Vente v WHERE v.dateVente BETWEEN :startDate AND :endDate")
    Long getNombreVentesPeriode(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT v FROM Vente v WHERE v.nomClient LIKE %:nomClient% OR v.telephoneClient LIKE %:telephoneClient%")
    List<Vente> findByClient(String nomClient, String telephoneClient);

    @Query("SELECT v FROM Vente v ORDER BY v.dateVente DESC")
    List<Vente> findRecentVentes(int limit);
}
