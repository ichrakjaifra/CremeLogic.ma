package ma.cremelogic.CremeLogic.ma.repository;

import ma.cremelogic.CremeLogic.ma.entity.CommandeAchat;
import ma.cremelogic.CremeLogic.ma.enums.StatutCommande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CommandeAchatRepository extends JpaRepository<CommandeAchat, Long> {
    Optional<CommandeAchat> findByNumeroCommande(String numeroCommande);
    List<CommandeAchat> findByFournisseurId(Long fournisseurId);
    List<CommandeAchat> findByStatut(StatutCommande statut);
    List<CommandeAchat> findByCreateurId(Long createurId);

    @Query("SELECT c FROM CommandeAchat c WHERE c.dateCommande BETWEEN :startDate AND :endDate")
    List<CommandeAchat> findByDateCommandeBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT c FROM CommandeAchat c WHERE c.dateLivraisonPrevue < CURRENT_DATE AND c.statut != 'LIVREE'")
    List<CommandeAchat> findCommandesEnRetard();

    @Query("SELECT SUM(c.montantTotal) FROM CommandeAchat c WHERE c.dateCommande BETWEEN :startDate AND :endDate")
    Double getMontantTotalCommandesPeriode(LocalDate startDate, LocalDate endDate);

    @Query("SELECT c FROM CommandeAchat c ORDER BY c.dateCreation DESC")
    List<CommandeAchat> findRecentCommandes(int limit);
}
