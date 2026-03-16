package ma.cremelogic.CremeLogic.ma.repository;

import ma.cremelogic.CremeLogic.ma.entity.OrdreProduction;
import ma.cremelogic.CremeLogic.ma.enums.StatutProduction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrdreProductionRepository extends JpaRepository<OrdreProduction, Long> {
    Optional<OrdreProduction> findByNumeroOrdre(String numeroOrdre);

    List<OrdreProduction> findAllByOrderByDateCreationDesc();

    List<OrdreProduction> findByProduitIdOrderByDateCreationDesc(Long produitId);

    List<OrdreProduction> findByStatutOrderByDateCreationDesc(StatutProduction statut);

    List<OrdreProduction> findByCreateurId(Long createurId);

    List<OrdreProduction> findByResponsableId(Long responsableId);

    @Query("SELECT o FROM OrdreProduction o WHERE o.dateCreation = :date")
    List<OrdreProduction> findByDateCreation(LocalDate date);

    @Query("SELECT SUM(o.quantite) FROM OrdreProduction o WHERE o.produit.id = :produitId AND o.statut = 'TERMINEE' AND o.dateFinReelle BETWEEN :startDate AND :endDate")
    Integer getQuantiteProduitePeriode(Long produitId, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT o FROM OrdreProduction o WHERE o.dateDebutPrevue BETWEEN :startDate AND :endDate")
    List<OrdreProduction> findByDateDebutBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT o FROM OrdreProduction o WHERE o.dateFinPrevue < CURRENT_DATE AND o.statut != 'TERMINEE'")
    List<OrdreProduction> findOrdresEnRetard();

    @Query("SELECT o FROM OrdreProduction o WHERE o.dateCreation >= :date")
    List<OrdreProduction> findOrdresDepuis(LocalDate date);

    @Query("SELECT SUM(o.coutTotal) FROM OrdreProduction o WHERE o.dateCreation BETWEEN :startDate AND :endDate")
    Double getCoutTotalProductionPeriode(LocalDate startDate, LocalDate endDate);

    @Query("SELECT SUM(o.quantite) FROM OrdreProduction o WHERE o.produit.id = :produitId AND o.statut = 'TERMINEE'")
    Integer getQuantiteProduite(Long produitId);
}