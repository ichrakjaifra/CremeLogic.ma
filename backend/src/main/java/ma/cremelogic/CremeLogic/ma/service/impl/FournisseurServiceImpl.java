package ma.cremelogic.CremeLogic.ma.service.impl;

import ma.cremelogic.CremeLogic.ma.dto.request.FournisseurRequest;
import ma.cremelogic.CremeLogic.ma.dto.response.FournisseurResponse;
import ma.cremelogic.CremeLogic.ma.entity.Fournisseur;
import ma.cremelogic.CremeLogic.ma.exception.ResourceNotFoundException;
import ma.cremelogic.CremeLogic.ma.exception.ValidationException;
import ma.cremelogic.CremeLogic.ma.repository.FournisseurRepository;
import ma.cremelogic.CremeLogic.ma.repository.CommandeAchatRepository;
import ma.cremelogic.CremeLogic.ma.service.interfaces.FournisseurService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FournisseurServiceImpl implements FournisseurService {

    private final FournisseurRepository fournisseurRepository;
    private final CommandeAchatRepository commandeAchatRepository;
    private final HistoriqueService historiqueService;

    @Override
    @Transactional
    public FournisseurResponse createFournisseur(FournisseurRequest request) {
        // Vérifier l'unicité de l'email
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            if (fournisseurRepository.existsByEmail(request.getEmail())) {
                throw new ValidationException("Un fournisseur avec cet email existe déjà");
            }
        }

        // Vérifier l'unicité du téléphone
        if (fournisseurRepository.existsByTelephone(request.getTelephone())) {
            throw new ValidationException("Un fournisseur avec ce numéro de téléphone existe déjà");
        }

        // Créer le fournisseur
        Fournisseur fournisseur = Fournisseur.builder()
                .nom(request.getNom())
                .telephone(request.getTelephone())
                .email(request.getEmail())
                .adresse(request.getAdresse())
                .ville(request.getVille())
                .pays(request.getPays())
                .codePostal(request.getCodePostal())
                .notes(request.getNotes())
                .noteEvaluation(request.getNoteEvaluation())
                .actif(request.isActif())
                .build();

        Fournisseur savedFournisseur = fournisseurRepository.save(fournisseur);

        // Historique
        historiqueService.enregistrerCreation("FOURNISSEUR", savedFournisseur.getId(),
                "Création du fournisseur: " + savedFournisseur.getNom());

        log.info("Fournisseur créé: {}", savedFournisseur.getNom());
        return mapToResponse(savedFournisseur);
    }

    @Override
    @Transactional
    public FournisseurResponse updateFournisseur(Long id, FournisseurRequest request) {
        Fournisseur fournisseur = fournisseurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fournisseur", "id", id));

        // Vérifier l'unicité de l'email
        if (request.getEmail() != null && !request.getEmail().isEmpty() &&
                !request.getEmail().equals(fournisseur.getEmail())) {
            if (fournisseurRepository.existsByEmail(request.getEmail())) {
                throw new ValidationException("Un fournisseur avec cet email existe déjà");
            }
        }

        // Vérifier l'unicité du téléphone
        if (!request.getTelephone().equals(fournisseur.getTelephone()) &&
                fournisseurRepository.existsByTelephone(request.getTelephone())) {
            throw new ValidationException("Un fournisseur avec ce numéro de téléphone existe déjà");
        }

        // Sauvegarder ancien nom pour historique
        String ancienNom = fournisseur.getNom();

        // Mettre à jour
        fournisseur.setNom(request.getNom());
        fournisseur.setTelephone(request.getTelephone());
        fournisseur.setEmail(request.getEmail());
        fournisseur.setAdresse(request.getAdresse());
        fournisseur.setVille(request.getVille());
        fournisseur.setPays(request.getPays());
        fournisseur.setCodePostal(request.getCodePostal());
        fournisseur.setNotes(request.getNotes());
        fournisseur.setNoteEvaluation(request.getNoteEvaluation());
        fournisseur.setActif(request.isActif());

        Fournisseur updatedFournisseur = fournisseurRepository.save(fournisseur);

        // Historique
        historiqueService.enregistrerModification("FOURNISSEUR", id,
                String.format("Mise à jour: %s -> %s", ancienNom, fournisseur.getNom()));

        log.info("Fournisseur mis à jour: {}", fournisseur.getNom());
        return mapToResponse(updatedFournisseur);
    }

    @Override
    public FournisseurResponse getFournisseur(Long id) {
        Fournisseur fournisseur = fournisseurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fournisseur", "id", id));
        return mapToResponse(fournisseur);
    }

    @Override
    public List<FournisseurResponse> getAllFournisseurs() {
        return fournisseurRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<FournisseurResponse> getFournisseursActifs() {
        return fournisseurRepository.findByActif(true).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<FournisseurResponse> searchFournisseurs(String keyword) {
        return fournisseurRepository.findByNomContainingIgnoreCase(keyword).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public void deleteFournisseur(Long id) {
        Fournisseur fournisseur = fournisseurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fournisseur", "id", id));

        // Vérifier si le fournisseur a des commandes
        if (!fournisseur.getCommandes().isEmpty()) {
            throw new ValidationException("Impossible de supprimer un fournisseur avec des commandes associées");
        }

        // Vérifier si le fournisseur a des ingrédients
        if (!fournisseur.getIngredients().isEmpty()) {
            throw new ValidationException("Impossible de supprimer un fournisseur avec des ingrédients associés");
        }

        fournisseurRepository.delete(fournisseur);
        historiqueService.enregistrerSuppression("FOURNISSEUR", id, "Suppression du fournisseur: " + fournisseur.getNom());
        log.info("Fournisseur supprimé: {}", fournisseur.getNom());
    }

    @Override
    @Transactional
    public FournisseurResponse evaluerFournisseur(Long id, Double note, String commentaire) {
        Fournisseur fournisseur = fournisseurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fournisseur", "id", id));

        if (note < 0 || note > 5) {
            throw new ValidationException("La note doit être comprise entre 0 et 5");
        }

        // Calculer la nouvelle note moyenne
        Double ancienneNote = fournisseur.getNoteEvaluation();
        if (ancienneNote == null) {
            fournisseur.setNoteEvaluation(note);
        } else {
            // Moyenne pondérée (simplifiée)
            fournisseur.setNoteEvaluation((ancienneNote + note) / 2);
        }

        // Ajouter le commentaire aux notes
        if (commentaire != null && !commentaire.isEmpty()) {
            String notesActuelles = fournisseur.getNotes() != null ? fournisseur.getNotes() : "";
            notesActuelles += "\nÉvaluation (" + java.time.LocalDate.now() + "): " + commentaire;
            fournisseur.setNotes(notesActuelles);
        }

        Fournisseur updatedFournisseur = fournisseurRepository.save(fournisseur);

        // Historique
        historiqueService.enregistrerModification("FOURNISSEUR", id,
                String.format("Évaluation: note %.2f/5, commentaire: %s", note, commentaire));

        return mapToResponse(updatedFournisseur);
    }

    @Override
    public List<FournisseurResponse> getFournisseursParVille(String ville) {
        return fournisseurRepository.findByVille(ville).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public BigDecimal getMontantTotalCommandes(Long fournisseurId) {
        Fournisseur fournisseur = fournisseurRepository.findById(fournisseurId)
                .orElseThrow(() -> new ResourceNotFoundException("Fournisseur", "id", fournisseurId));

        return fournisseur.getMontantTotalCommandes();
    }

    @Override
    public Integer getNombreCommandes(Long fournisseurId) {
        Fournisseur fournisseur = fournisseurRepository.findById(fournisseurId)
                .orElseThrow(() -> new ResourceNotFoundException("Fournisseur", "id", fournisseurId));

        return fournisseur.getNombreCommandes();
    }

    @Override
    @Transactional
    public FournisseurResponse toggleActif(Long id) {
        Fournisseur fournisseur = fournisseurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fournisseur", "id", id));

        boolean nouvelEtat = !fournisseur.isActif();
        fournisseur.setActif(nouvelEtat);

        Fournisseur updatedFournisseur = fournisseurRepository.save(fournisseur);

        // Historique
        historiqueService.enregistrerModification("FOURNISSEUR", id,
                String.format("Changement statut: %s -> %s",
                        !nouvelEtat ? "Actif" : "Inactif",
                        nouvelEtat ? "Actif" : "Inactif"));

        return mapToResponse(updatedFournisseur);
    }

    private FournisseurResponse mapToResponse(Fournisseur fournisseur) {
        return FournisseurResponse.builder()
                .id(fournisseur.getId())
                .nom(fournisseur.getNom())
                .telephone(fournisseur.getTelephone())
                .email(fournisseur.getEmail())
                .adresse(fournisseur.getAdresse())
                .ville(fournisseur.getVille())
                .pays(fournisseur.getPays())
                .codePostal(fournisseur.getCodePostal())
                .notes(fournisseur.getNotes())
                .noteEvaluation(fournisseur.getNoteEvaluation())
                .actif(fournisseur.isActif())
                .nombreCommandes(fournisseur.getNombreCommandes())
                .montantTotalCommandes(fournisseur.getMontantTotalCommandes())
                .dateCreation(fournisseur.getDateCreation())
                .dateModification(fournisseur.getDateModification())
                .build();
    }
}
