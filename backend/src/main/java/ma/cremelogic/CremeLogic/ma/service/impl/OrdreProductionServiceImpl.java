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
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
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

        // Vérifier la disponibilité des ingrédients
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
        ordre.setDateFinPrevue(request.getDateFinPrevue());
        ordre.setNotes(request.getNotes());

        if (request.getResponsableId() != null) {
            Utilisateur responsable = utilisateurRepository.findById(request.getResponsableId())
                    .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "id", request.getResponsableId()));
            ordre.setResponsable(responsable);
        }

        ordre.calculerCouts();
        OrdreProduction updated = ordreProductionRepository.save(ordre);

        historiqueService.enregistrerModification("ORDRE_PRODUCTION", id,
                "Mise à jour de l'ordre de production");

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
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrdreProductionResponse> getOrdresByProduit(Long produitId) {
        return ordreProductionRepository.findByProduitIdOrderByDateCreationDesc(produitId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrdreProductionResponse> getOrdresByStatut(String statut) {
        try {
            StatutProduction statutEnum = StatutProduction.valueOf(statut.toUpperCase());
            return ordreProductionRepository.findByStatutOrderByDateCreationDesc(statutEnum).stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
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

        StatutProduction ancienStatut = ordre.getStatut();
        StatutProduction nouveauStatut;

        try {
            nouveauStatut = StatutProduction.valueOf(statut.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Statut invalide: " + statut);
        }

        ordre.setStatut(nouveauStatut);
        OrdreProduction updated = ordreProductionRepository.save(ordre);

        historiqueService.enregistrerModification("ORDRE_PRODUCTION", id,
                String.format("Changement statut: %s -> %s", ancienStatut, nouveauStatut));

        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public OrdreProductionResponse demarrerProduction(Long id, ExecutionProductionRequest request) {
        OrdreProduction ordre = ordreProductionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ordre de production", "id", id));

        if (ordre.getStatut() != StatutProduction.PLANIFIEE) {
            throw new ValidationException("Seuls les ordres planifiés peuvent être démarrés");
        }

        // Vérifier la disponibilité des ingrédients
        verifierDisponibiliteIngredients(ordre);

        ordre.setDateDebutReelle(request.getDateDebutReelle());
        ordre.setStatut(StatutProduction.EN_COURS);

        if (request.getNotes() != null && !request.getNotes().isEmpty()) {
            ordre.setNotes(ordre.getNotes() + "\n" + request.getNotes());
        }

        OrdreProduction updated = ordreProductionRepository.save(ordre);

        historiqueService.enregistrerModification("ORDRE_PRODUCTION", id,
                "Démarrage de la production");

        log.info("Production démarrée: {}", ordre.getNumeroOrdre());
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

        // Consommer les ingrédients
        consommerIngredients(ordre);

        ordre.setDateFinReelle(request.getDateDebutReelle()); // Utiliser dateDebutReelle comme date de fin
        ordre.setStatut(StatutProduction.TERMINEE);

        if (request.getNotes() != null && !request.getNotes().isEmpty()) {
            ordre.setNotes(ordre.getNotes() + "\nTERMINÉ: " + request.getNotes());
        }

        // Mettre à jour le stock du produit
        Produit produit = ordre.getProduit();
        produit.setStockDisponible(produit.getStockDisponible() + ordre.getQuantite());
        produitRepository.save(produit);

        OrdreProduction updated = ordreProductionRepository.save(ordre);

        historiqueService.enregistrerModification("ORDRE_PRODUCTION", id,
                "Production terminée - Quantité: " + ordre.getQuantite());

        log.info("Production terminée: {} - {} unités produites",
                ordre.getNumeroOrdre(), ordre.getQuantite());
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

        // Créer une alerte
        alerteService.creerAlerteProductionAnnulee(ordre, raison);

        historiqueService.enregistrerModification("ORDRE_PRODUCTION", id,
                "Annulation de la production: " + raison);

        log.info("Production annulée: {} - Raison: {}", ordre.getNumeroOrdre(), raison);
        return mapToResponse(updated);
    }

    @Override
    public List<OrdreProductionResponse> getOrdresEnRetard() {
        return ordreProductionRepository.findOrdresEnRetard().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BigDecimal getCoutTotalProductionPeriode(LocalDate debut, LocalDate fin) {
        Double cout = ordreProductionRepository.getCoutTotalProductionPeriode(debut, fin);
        return cout != null ? BigDecimal.valueOf(cout) : BigDecimal.ZERO;
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

        Utilisateur createur = getCurrentUser();

        OrdreProduction nouvelOrdre = OrdreProduction.builder()
                .produit(ordreOriginal.getProduit())
                .quantite(ordreOriginal.getQuantite())
                .dateDebutPrevue(ordreOriginal.getDateDebutPrevue())
                .dateFinPrevue(ordreOriginal.getDateFinPrevue())
                .notes("DUPLICATA de " + ordreOriginal.getNumeroOrdre() + "\n" + ordreOriginal.getNotes())
                .createur(createur)
                .responsable(ordreOriginal.getResponsable())
                .statut(StatutProduction.PLANIFIEE)
                .build();

        nouvelOrdre.calculerCouts();
        OrdreProduction saved = ordreProductionRepository.save(nouvelOrdre);

        historiqueService.enregistrerCreation("ORDRE_PRODUCTION", saved.getId(),
                "Duplication de l'ordre: " + ordreOriginal.getNumeroOrdre());

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
        if (recette == null) {
            throw new ValidationException("Le produit n'a pas de recette associée");
        }

        Map<Long, BigDecimal> consommations = new HashMap<>();
        BigDecimal facteurMultiplicateur = BigDecimal.valueOf(ordre.getQuantite())
                .divide(BigDecimal.valueOf(recette.getNombrePortions()), 4, BigDecimal.ROUND_HALF_UP);

        for (LigneRecette ligne : recette.getLignesRecette()) {
            BigDecimal quantiteNecessaire = ligne.getQuantite().multiply(facteurMultiplicateur);
            consommations.put(ligne.getIngredient().getId(), quantiteNecessaire);
        }

        mouvementStockService.deduireConsommationProduction(ordre.getId(), consommations);

        log.info("Ingrédients consommés pour l'ordre de production {}", ordre.getNumeroOrdre());
    }

    private void verifierDisponibiliteIngredients(OrdreProduction ordre) {
        Recette recette = ordre.getProduit().getRecette();
        if (recette == null) return;

        BigDecimal facteurMultiplicateur = BigDecimal.valueOf(ordre.getQuantite())
                .divide(BigDecimal.valueOf(recette.getNombrePortions()), 4, BigDecimal.ROUND_HALF_UP);

        List<String> manquants = recette.getLignesRecette().stream()
                .filter(ligne -> {
                    BigDecimal quantiteNecessaire = ligne.getQuantite().multiply(facteurMultiplicateur);
                    return ligne.getIngredient().getQuantiteStock().compareTo(quantiteNecessaire) < 0;
                })
                .map(ligne -> ligne.getIngredient().getNom())
                .collect(Collectors.toList());

        if (!manquants.isEmpty()) {
            log.warn("Ingrédients insuffisants pour {}: {}", ordre.getNumeroOrdre(), manquants);
            // Optionnel: créer une alerte
            alerteService.creerAlerteIngredientsInsuffisants(ordre, manquants);
        }
    }

    private Utilisateur getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "email", email));
    }

    private OrdreProductionResponse mapToResponse(OrdreProduction ordre) {
        return OrdreProductionResponse.builder()
                .id(ordre.getId())
                .numeroOrdre(ordre.getNumeroOrdre())
                .produitId(ordre.getProduit().getId())
                .produitNom(ordre.getProduit().getNom())
                .quantite(ordre.getQuantite())
                .dateDebutPrevue(ordre.getDateDebutPrevue())
                .dateFinPrevue(ordre.getDateFinPrevue())
                .dateDebutReelle(ordre.getDateDebutReelle())
                .dateFinReelle(ordre.getDateFinReelle())
                .statut(ordre.getStatut())
                .coutTotal(ordre.getCoutTotal())
                .coutUnitaire(ordre.getCoutUnitaire())
                .notes(ordre.getNotes())
                .createurId(ordre.getCreateur() != null ? ordre.getCreateur().getId() : null)
                .createurNom(ordre.getCreateur() != null ?
                        ordre.getCreateur().getNom() + " " + ordre.getCreateur().getPrenom() : null)
                .responsableId(ordre.getResponsable() != null ? ordre.getResponsable().getId() : null)
                .responsableNom(ordre.getResponsable() != null ?
                        ordre.getResponsable().getNom() + " " + ordre.getResponsable().getPrenom() : null)
                .dateCreation(ordre.getDateCreation())
                .dateModification(ordre.getDateModification())
                .enRetard(ordre.estEnRetard())
                .build();
    }
}