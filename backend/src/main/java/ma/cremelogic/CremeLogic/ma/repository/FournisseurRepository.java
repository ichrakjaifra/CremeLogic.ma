package ma.cremelogic.CremeLogic.ma.repository;

import ma.cremelogic.CremeLogic.ma.entity.Fournisseur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FournisseurRepository extends JpaRepository<Fournisseur, Long> {
    Optional<Fournisseur> findByEmail(String email);
    Optional<Fournisseur> findByTelephone(String telephone);
    List<Fournisseur> findByNomContainingIgnoreCase(String nom);
    List<Fournisseur> findByVille(String ville);
    List<Fournisseur> findByActif(boolean actif);

    @Query("SELECT f FROM Fournisseur f WHERE f.noteEvaluation >= :minNote")
    List<Fournisseur> findByNoteEvaluationGreaterThanEqual(Double minNote);

    @Query("SELECT COUNT(f) FROM Fournisseur f WHERE f.actif = true")
    Long countActifs();

    boolean existsByEmail(String email);
    boolean existsByTelephone(String telephone);
}