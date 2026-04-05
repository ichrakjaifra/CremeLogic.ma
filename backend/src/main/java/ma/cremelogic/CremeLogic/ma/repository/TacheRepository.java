package ma.cremelogic.CremeLogic.ma.repository;

import ma.cremelogic.CremeLogic.ma.entity.Tache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TacheRepository extends JpaRepository<Tache, Long> {
    List<Tache> findByAssigneAId(Long utilisateurId);
    List<Tache> findByAssigneAIdAndDateEcheanceBetween(Long utilisateurId, java.time.LocalDateTime start, java.time.LocalDateTime end);
    List<Tache> findByStatut(String statut);
}
