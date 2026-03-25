package ma.cremelogic.CremeLogic.ma.repository;

import ma.cremelogic.CremeLogic.ma.entity.SuiviEtape;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SuiviEtapeRepository extends JpaRepository<SuiviEtape, Long> {
    List<SuiviEtape> findByOrdreProductionIdOrderByEtapeRecetteOrdreAsc(Long ordreProductionId);
}
