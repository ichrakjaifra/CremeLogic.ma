package ma.cremelogic.CremeLogic.ma.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.cremelogic.CremeLogic.ma.dto.request.ExecutionProductionRequest;
import ma.cremelogic.CremeLogic.ma.dto.request.OrdreProductionRequest;
import ma.cremelogic.CremeLogic.ma.dto.response.OrdreProductionResponse;
import ma.cremelogic.CremeLogic.ma.entity.*;
import ma.cremelogic.CremeLogic.ma.enums.StatutProduction;
import ma.cremelogic.CremeLogic.ma.exception.ResourceNotFoundException;
import ma.cremelogic.CremeLogic.ma.exception.ValidationException;
import ma.cremelogic.CremeLogic.ma.repository.*;
import ma.cremelogic.CremeLogic.ma.service.interfaces.AlerteService;
import ma.cremelogic.CremeLogic.ma.service.interfaces.MouvementStockService;
import ma.cremelogic.CremeLogic.ma.service.interfaces.OrdreProductionService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class OrdreProductionServiceImpl implements OrdreProductionService {

    private final OrdreProductionRepository ordreProductionRepository;
    private final ProduitRepository produitRepository;
    private final RecetteRepository recetteRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final MouvementStockService mouvementStockService;
    private final AlerteService alerteService;
    private final HistoriqueService historiqueService;

    @Override
    @Transactional
    public OrdreProductionResponse createOrdre(OrdreProductionRequest request) {
        Produit produit = produitRepository.findById(request.getProduitId())
                .orElseThrow(() -> new ResourceNotFoundException("Produit", "id", request.getProduitId()));

        if (produit.getRecette() == null) {
            throw new ValidationException("Le produit n'a pas de recette associée");
        }

        Utilisateur createur = getCurrentUser();

        OrdreProduction ordre = OrdreProduction.builder()
                .produit(produit)
                .quantite(request.getQuantite())
                .dateDebutPrevue(request.getDateDebutPrevue())
                .dateFinPrevue(request.getDateFinPrevue())
                .notes(request.getNotes())
                .createur(createur)
                .statut(StatutProduction.PLANIFIEE)
                .build();

        if (request.getResponsableId() != null) {
            Utilisateur responsable = utilisateurRepository.findById(request.getResponsableId())
                    .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "id", request.getResponsableId()));
            ordre.setResponsable(responsable);
        }

        ordre.calculerCouts();
        OrdreProduction saved = ordreProductionRepository.save(ordre);

        verifierDisponibiliteIngredients(ordre);

        historiqueService.enregistrerCreation("ORDRE_PRODUCTION", saved.getId(),
                "Création de l'ordre de production: " + saved.getNumeroOrdre() +
                        " pour " + produit.getNom());

        log.info("Ordre de production créé: {} pour {}", saved.getNumeroOrdre(), produit.getNom());
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public OrdreProductionResponse updateOrdre(Long id, OrdreProductionRequest request) {
        OrdreProduction ordre = ordreProductionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ordre de production", "id", id));

        if (ordre.getStatut() != StatutProduction.PLANIFIEE) {
            throw new ValidationException("Impossible de modifier un ordre de production qui n'est pas planifié");
        }

        Produit produit = produitRepository.findById(request.getProduitId())
                .orElseThrow(() -> new ResourceNotFoundException("Produit", "id", request.getProduitId()));

        ordre.setProduit(produit);
        ordre.setQuantite(request.getQuantite());
        ordre.setDateDebutPrevue(request.getDateDebutPrevue());
        ordre.setNotes(request.getNotes());

        if (request.getResponsableId() != null) {
            Utilisateur responsable = utilisateurRepository.findById(request.getResponsableId())
                    .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "id", request.getResponsableId()));
            ordre.setResponsable(responsable);
        }

        ordre.calculerCouts();
        OrdreProduction updated = ordreProductionRepository.save(ordre);

        historiqueService.enregistrerModification("ORDRE_PRODUCTION", id, "Mise à jour de l'ordre de production");
        return mapToResponse(updated);
    }

    @Override
    public OrdreProductionResponse getOrdre(Long id) {
        OrdreProduction ordre = ordreProductionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ordre de production", "id", id));
        return mapToResponse(ordre);
    }

    @Override
    public List<OrdreProductionResponse> getAllOrdres() {
        return ordreProductionRepository.findAllByOrderByDateCreationDesc().stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<OrdreProductionResponse> getOrdresByProduit(Long produitId) {
        return ordreProductionRepository.findByProduitIdOrderByDateCreationDesc(produitId).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<OrdreProductionResponse> getOrdresByStatut(String statut) {
        try {
            StatutProduction statutEnum = StatutProduction.valueOf(statut.toUpperCase());
            return ordreProductionRepository.findByStatutOrderByDateCreationDesc(statutEnum).stream()
                    .map(this::mapToResponse).collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Statut invalide: " + statut);
        }
    }

    @Override
    @Transactional
    public void deleteOrdre(Long id) {
        OrdreProduction ordre = ordreProductionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ordre de production", "id", id));

        if (ordre.getStatut() == StatutProduction.EN_COURS) {
            throw new ValidationException("Impossible de supprimer un ordre en cours");
        }

        ordreProductionRepository.delete(ordre);
        historiqueService.enregistrerSuppression("ORDRE_PRODUCTION", id,
                "Suppression de l'ordre: " + ordre.getNumeroOrdre());
    }

    @Override
    @Transactional
    public OrdreProductionResponse changerStatut(Long id, String statut) {
        OrdreProduction ordre = ordreProductionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ordre de production", "id", id));

        try {
            StatutProduction nouveauStatut = StatutProduction.valueOf(statut.toUpperCase());
            ordre.setStatut(nouveauStatut);
            OrdreProduction updated = ordreProductionRepository.save(ordre);
            historiqueService.enregistrerModification("ORDRE_PRODUCTION", id, "Changement statut -> " + nouveauStatut);
            return mapToResponse(updated);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Statut invalide: " + statut);
        }
    }

    @Override
    @Transactional
    public OrdreProductionResponse demarrerProduction(Long id, ExecutionProductionRequest request) {
        OrdreProduction ordre = ordreProductionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ordre de production", "id", id));

        if (ordre.getStatut() != StatutProduction.PLANIFIEE) {
            throw new ValidationException("Seuls les ordres planifiés peuvent être démarrés");
        }

        verifierDisponibiliteIngredients(ordre);
        ordre.setDateDebutReelle(request.getDateDebutReelle());
        ordre.setStatut(StatutProduction.EN_COURS);
        if (request.getNotes() != null)
            ordre.setNotes(ordre.getNotes() + "\n" + request.getNotes());

        OrdreProduction updated = ordreProductionRepository.save(ordre);
        historiqueService.enregistrerModification("ORDRE_PRODUCTION", id, "Démarrage de la production");
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public OrdreProductionResponse terminerProduction(Long id, ExecutionProductionRequest request) {
        OrdreProduction ordre = ordreProductionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ordre de production", "id", id));

        if (ordre.getStatut() != StatutProduction.EN_COURS) {
            throw new ValidationException("Seuls les ordres en cours peuvent être terminés");
        }

        consommerIngredients(ordre);
        ordre.setDateFinReelle(request.getDateDebutReelle());
        ordre.setStatut(StatutProduction.TERMINEE);
        if (request.getNotes() != null)
            ordre.setNotes(ordre.getNotes() + "\nTERMINÉ: " + request.getNotes());

        Produit produit = ordre.getProduit();
        produit.setStockDisponible(produit.getStockDisponible() + ordre.getQuantite());
        produitRepository.save(produit);

        OrdreProduction updated = ordreProductionRepository.save(ordre);
        historiqueService.enregistrerModification("ORDRE_PRODUCTION", id, "Production terminée");
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public OrdreProductionResponse annulerProduction(Long id, String raison) {
        OrdreProduction ordre = ordreProductionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ordre de production", "id", id));

        if (ordre.getStatut() == StatutProduction.TERMINEE) {
            throw new ValidationException("Impossible d'annuler une production terminée");
        }

        ordre.setStatut(StatutProduction.ANNULEE);
        ordre.setNotes(ordre.getNotes() + "\nANNULATION: " + raison);
        OrdreProduction updated = ordreProductionRepository.save(ordre);
        alerteService.creerAlerteProductionAnnulee(ordre, raison);
        historiqueService.enregistrerModification("ORDRE_PRODUCTION", id, "Annulation de la production");
        return mapToResponse(updated);
    }

    @Override
    public List<OrdreProductionResponse> getOrdresEnRetard() {
        return ordreProductionRepository.findOrdresEnRetard().stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public BigDecimal getCoutTotalProductionPeriode(LocalDate debut, LocalDate fin) {
        BigDecimal cout = ordreProductionRepository.getCoutTotalProductionPeriode(debut.atStartOfDay(), fin.atTime(23, 59, 59));
        return cout != null ? cout : BigDecimal.ZERO;
    }

    @Override
    public Integer getQuantiteProduite(Long produitId, LocalDate debut, LocalDate fin) {
        return ordreProductionRepository.getQuantiteProduitePeriode(produitId, debut, fin);
    }

    @Override
    @Transactional
    public OrdreProductionResponse dupliquerOrdre(Long id) {
        OrdreProduction ordreOriginal = ordreProductionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ordre de production", "id", id));

        OrdreProduction nouvelOrdre = OrdreProduction.builder()
                .produit(ordreOriginal.getProduit()).quantite(ordreOriginal.getQuantite())
                .dateDebutPrevue(ordreOriginal.getDateDebutPrevue()).dateFinPrevue(ordreOriginal.getDateFinPrevue())
                .notes("DUPLICATA de " + ordreOriginal.getNumeroOrdre()).createur(getCurrentUser())
                .responsable(ordreOriginal.getResponsable()).statut(StatutProduction.PLANIFIEE).build();

        nouvelOrdre.calculerCouts();
        OrdreProduction saved = ordreProductionRepository.save(nouvelOrdre);
        historiqueService.enregistrerCreation("ORDRE_PRODUCTION", saved.getId(), "Duplication de l'ordre");
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public void consommerIngredients(Long ordreProductionId) {
        OrdreProduction ordre = ordreProductionRepository.findById(ordreProductionId)
                .orElseThrow(() -> new ResourceNotFoundException("Ordre de production", "id", ordreProductionId));
        consommerIngredients(ordre);
    }

    private void consommerIngredients(OrdreProduction ordre) {
        Recette recette = ordre.getProduit().getRecette();
        if (recette == null)
            throw new ValidationException("Le produit n'a pas de recette associée");

        Map<Long, BigDecimal> consommations = new HashMap<>();
        BigDecimal facteurMultiplicateur = BigDecimal.valueOf(ordre.getQuantite())
                .divide(BigDecimal.valueOf(recette.getNombrePortions()), 4, RoundingMode.HALF_UP);

        for (LigneRecette ligne : recette.getLignesRecette()) {
            consommations.put(ligne.getIngredient().getId(), ligne.getQuantite().multiply(facteurMultiplicateur));
        }

        mouvementStockService.deduireConsommationProduction(ordre.getId(), consommations);
    }

    private void verifierDisponibiliteIngredients(OrdreProduction ordre) {
        Recette recette = ordre.getProduit().getRecette();
        if (recette == null)
            return;

        BigDecimal facteurMultiplicateur = BigDecimal.valueOf(ordre.getQuantite())
                .divide(BigDecimal.valueOf(recette.getNombrePortions()), 4, RoundingMode.HALF_UP);

        List<String> manquants = recette.getLignesRecette().stream()
                .filter(l -> l.getIngredient().getQuantiteStock()
                        .compareTo(l.getQuantite().multiply(facteurMultiplicateur)) < 0)
                .map(l -> l.getIngredient().getNom()).toList();

        if (!manquants.isEmpty())
            alerteService.creerAlerteIngredientsInsuffisants(ordre, manquants);
    }

    private Utilisateur getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "email", email));
    }

    private OrdreProductionResponse mapToResponse(OrdreProduction ordre) {
        return OrdreProductionResponse.builder()
                .id(ordre.getId()).numeroOrdre(ordre.getNumeroOrdre()).produitId(ordre.getProduit().getId())
                .produitNom(ordre.getProduit().getNom()).quantite(ordre.getQuantite())
                .dateDebutPrevue(ordre.getDateDebutPrevue()).dateFinPrevue(ordre.getDateFinPrevue())
                .dateDebutReelle(ordre.getDateDebutReelle()).dateFinReelle(ordre.getDateFinReelle())
                .statut(ordre.getStatut()).coutTotal(ordre.getCoutTotal()).coutUnitaire(ordre.getCoutUnitaire())
                .notes(ordre.getNotes()).createurId(ordre.getCreateur() != null ? ordre.getCreateur().getId() : null)
                .createurNom(ordre.getCreateur() != null ? ordre.getCreateur().getNom() : null)
                .responsableId(ordre.getResponsable() != null ? ordre.getResponsable().getId() : null)
                .responsableNom(ordre.getResponsable() != null ? ordre.getResponsable().getNom() : null)
                .dateCreation(ordre.getDateCreation()).dateModification(ordre.getDateModification())
                .enRetard(ordre.estEnRetard()).build();
    }
}