package ma.cremelogic.CremeLogic.ma.service;

import lombok.RequiredArgsConstructor;
import ma.cremelogic.CremeLogic.ma.entity.Tache;
import ma.cremelogic.CremeLogic.ma.repository.UtilisateurRepository;
import ma.cremelogic.CremeLogic.ma.repository.TacheRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class TacheService {

    private final TacheRepository tacheRepository;
    private final UtilisateurRepository utilisateurRepository;

    public List<Tache> getAll() {
        return tacheRepository.findAll();
    }

    public List<Tache> getByUtilisateur(Long utilisateurId) {
        return tacheRepository.findByAssigneAId(utilisateurId);
    }

    public Tache create(Tache tache) {
        if (tache.getTransientAssigneAId() != null) {
            utilisateurRepository.findById(tache.getTransientAssigneAId())
                    .ifPresent(tache::setAssigneA);
        }
        return tacheRepository.save(tache);
    }

    @Transactional
    public Tache update(Long id, Tache tacheDetails) {
        Tache tache = tacheRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tache non trouvée"));
        
        tache.setTitre(tacheDetails.getTitre());
        tache.setDescription(tacheDetails.getDescription());
        tache.setStatut(tacheDetails.getStatut());
        tache.setPriorite(tacheDetails.getPriorite());
        tache.setDateEcheance(tacheDetails.getDateEcheance());
        
        return tacheRepository.save(tache);
    }

    @Transactional
    public Tache updateStatut(Long id, String statut) {
        Tache tache = tacheRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tache non trouvée"));
        tache.setStatut(statut);
        return tacheRepository.save(tache);
    }

    public void delete(Long id) {
        tacheRepository.deleteById(id);
    }
}
