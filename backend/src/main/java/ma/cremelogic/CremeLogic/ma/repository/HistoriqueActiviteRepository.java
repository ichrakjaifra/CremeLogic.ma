package ma.cremelogic.CremeLogic.ma.repository;

import ma.cremelogic.CremeLogic.ma.entity.HistoriqueActivite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoriqueActiviteRepository extends JpaRepository<HistoriqueActivite, Long> {
    Page<HistoriqueActivite> findByUtilisateurIdOrderByDateActionDesc(Long utilisateurId, Pageable pageable);
}