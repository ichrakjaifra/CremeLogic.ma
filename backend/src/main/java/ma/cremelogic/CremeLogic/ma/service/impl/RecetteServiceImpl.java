package ma.cremelogic.CremeLogic.ma.service.impl;

import ma.cremelogic.CremeLogic.ma.dto.request.RecetteRequest;
import ma.cremelogic.CremeLogic.ma.dto.response.LigneRecetteResponse;
import ma.cremelogic.CremeLogic.ma.dto.response.RecetteResponse;
import ma.cremelogic.CremeLogic.ma.entity.Recette;
import ma.cremelogic.CremeLogic.ma.entity.LigneRecette;
import ma.cremelogic.CremeLogic.ma.entity.Ingredient;
import ma.cremelogic.CremeLogic.ma.entity.Utilisateur;
import ma.cremelogic.CremeLogic.ma.exception.ResourceNotFoundException;
import ma.cremelogic.CremeLogic.ma.exception.ValidationException;
import ma.cremelogic.CremeLogic.ma.repository.RecetteRepository;
import ma.cremelogic.CremeLogic.ma.repository.IngredientRepository;
import ma.cremelogic.CremeLogic.ma.repository.UtilisateurRepository;
import ma.cremelogic.CremeLogic.ma.service.interfaces.RecetteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecetteServiceImpl implements RecetteService {

    private final RecetteRepository recetteRepository;
    private final IngredientRepository ingredientRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final HistoriqueService historiqueService;

    @Override
    @Transactional
    public RecetteResponse createRecette(RecetteRequest request) {
        // Récupérer l'utilisateur connecté
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Utilisateur createur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "email", email));

        // Créer la recette
        Recette recette = Recette.builder()
                .nom(request.getNom())
                .description(request.getDescription())
                .instructions(request.getInstructions())
                .tempsPreparation(request.getTempsPreparation())
                .tempsCuisson(request.getTempsCuisson())
                .nombrePortions(request.getNombrePortions())
                .createur(createur)
                .lignesRecette(new ArrayList<>())
                .build();

        // Ajouter les lignes de recette
        for (var ligneRequest : request.getLignesRecette()) {
            Ingredient ingredient = ingredientRepository.findById(ligneRequest.getIngredientId())
                    .orElseThrow(() -> new ResourceNotFoundException("Ingrédient", "id", ligneRequest.getIngredientId()));

            LigneRecette ligneRecette = LigneRecette.builder()
                    .recette(recette)
                    .ingredient(ingredient)
                    .quantite(ligneRequest.getQuantite())
                    .instructionsSpecifiques(ligneRequest.getInstructionsSpecifiques())
                    .build();

            recette.getLignesRecette().add(ligneRecette);
        }

        // Calculer le coût total
        recette.calculerCoutTotal();

        Recette savedRecette = recetteRepository.save(recette);

        // Historique
        historiqueService.enregistrerCreation("RECETTE", savedRecette.getId(),
                "Création de la recette: " + savedRecette.getNom());

        log.info("Recette créée: {} ({} ingrédients)", savedRecette.getNom(), savedRecette.getLignesRecette().size());
        return mapToResponse(savedRecette);
    }

    @Override
    @Transactional
    public RecetteResponse updateRecette(Long id, RecetteRequest request) {
        Recette recette = recetteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recette", "id", id));

        // Sauvegarder ancien nom pour historique
        String ancienNom = recette.getNom();

        // Mettre à jour les informations de base
        recette.setNom(request.getNom());
        recette.setDescription(request.getDescription());
        recette.setInstructions(request.getInstructions());
        recette.setTempsPreparation(request.getTempsPreparation());
        recette.setTempsCuisson(request.getTempsCuisson());
        recette.setNombrePortions(request.getNombrePortions());

        // Mettre à jour les lignes de recette
        recette.getLignesRecette().clear();

        for (var ligneRequest : request.getLignesRecette()) {
            Ingredient ingredient = ingredientRepository.findById(ligneRequest.getIngredientId())
                    .orElseThrow(() -> new ResourceNotFoundException("Ingrédient", "id", ligneRequest.getIngredientId()));

            LigneRecette ligneRecette = LigneRecette.builder()
                    .recette(recette)
                    .ingredient(ingredient)
                    .quantite(ligneRequest.getQuantite())
                    .instructionsSpecifiques(ligneRequest.getInstructionsSpecifiques())
                    .build();

            recette.getLignesRecette().add(ligneRecette);
        }

        // Recalculer le coût total
        recette.calculerCoutTotal();

        Recette updatedRecette = recetteRepository.save(recette);

        // Historique
        historiqueService.enregistrerModification("RECETTE", id,
                String.format("Mise à jour: %s -> %s (%d ingrédients)",
                        ancienNom, recette.getNom(), recette.getLignesRecette().size()));

        return mapToResponse(updatedRecette);
    }

    @Override
    public RecetteResponse getRecette(Long id) {
        Recette recette = recetteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recette", "id", id));
        return mapToResponse(recette);
    }

    @Override
    public List<RecetteResponse> getAllRecettes() {
        return recetteRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<RecetteResponse> getRecettesByCreateur(Long createurId) {
        return recetteRepository.findByCreateurId(createurId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<RecetteResponse> searchRecettes(String keyword) {
        return recetteRepository.findByNomContainingIgnoreCase(keyword).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public void deleteRecette(Long id) {
        Recette recette = recetteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recette", "id", id));

        // Vérifier si la recette est utilisée par un produit
        if (recette.getProduit() != null) {
            throw new ValidationException("Impossible de supprimer une recette utilisée par un produit");
        }

        recetteRepository.delete(recette);
        historiqueService.enregistrerSuppression("RECETTE", id, "Suppression de la recette: " + recette.getNom());
        log.info("Recette supprimée: {}", recette.getNom());
    }

    @Override
    @Transactional
    public RecetteResponse dupliquerRecette(Long id, String nouveauNom) {
        Recette recetteOriginale = recetteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recette", "id", id));

        // Récupérer l'utilisateur connecté
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Utilisateur createur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "email", email));

        // Créer une nouvelle recette copiée
        Recette recetteCopie = Recette.builder()
                .nom(nouveauNom != null ? nouveauNom : recetteOriginale.getNom() + " (Copie)")
                .description(recetteOriginale.getDescription())
                .instructions(recetteOriginale.getInstructions())
                .tempsPreparation(recetteOriginale.getTempsPreparation())
                .tempsCuisson(recetteOriginale.getTempsCuisson())
                .nombrePortions(recetteOriginale.getNombrePortions())
                .createur(createur)
                .lignesRecette(new ArrayList<>())
                .build();

        // Copier les lignes de recette
        for (LigneRecette ligneOriginale : recetteOriginale.getLignesRecette()) {
            LigneRecette ligneCopie = LigneRecette.builder()
                    .recette(recetteCopie)
                    .ingredient(ligneOriginale.getIngredient())
                    .quantite(ligneOriginale.getQuantite())
                    .instructionsSpecifiques(ligneOriginale.getInstructionsSpecifiques())
                    .build();

            recetteCopie.getLignesRecette().add(ligneCopie);
        }

        // Calculer le coût total
        recetteCopie.calculerCoutTotal();

        Recette savedRecette = recetteRepository.save(recetteCopie);

        // Historique
        historiqueService.enregistrerCreation("RECETTE", savedRecette.getId(),
                "Duplication de la recette: " + recetteOriginale.getNom() + " -> " + savedRecette.getNom());

        log.info("Recette dupliquée: {} -> {}", recetteOriginale.getNom(), savedRecette.getNom());
        return mapToResponse(savedRecette);
    }

    @Override
    @Transactional
    public BigDecimal calculerCoutRecette(Long recetteId) {
        Recette recette = recetteRepository.findById(recetteId)
                .orElseThrow(() -> new ResourceNotFoundException("Recette", "id", recetteId));

        recette.calculerCoutTotal();
        recetteRepository.save(recette);

        return recette.getCoutTotal();
    }

    @Override
    public List<RecetteResponse> getRecettesByIngredient(Long ingredientId) {
        return recetteRepository.findByIngredientId(ingredientId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public RecetteResponse ajusterPortions(Long recetteId, Integer nouvellesPortions) {
        if (nouvellesPortions <= 0) {
            throw new ValidationException("Le nombre de portions doit être supérieur à 0");
        }

        Recette recette = recetteRepository.findById(recetteId)
                .orElseThrow(() -> new ResourceNotFoundException("Recette", "id", recetteId));

        Integer anciennesPortions = recette.getNombrePortions();
        BigDecimal ratio = BigDecimal.valueOf(nouvellesPortions)
                .divide(BigDecimal.valueOf(anciennesPortions), 4, BigDecimal.ROUND_HALF_UP);

        // Ajuster les quantités des ingrédients
        for (LigneRecette ligne : recette.getLignesRecette()) {
            BigDecimal nouvelleQuantite = ligne.getQuantite().multiply(ratio);
            ligne.setQuantite(nouvelleQuantite);
        }

        // Mettre à jour le nombre de portions
        recette.setNombrePortions(nouvellesPortions);

        // Recalculer le coût total
        recette.calculerCoutTotal();

        Recette updatedRecette = recetteRepository.save(recette);

        // Historique
        historiqueService.enregistrerModification("RECETTE", recetteId,
                String.format("Ajustement portions: %d -> %d portions", anciennesPortions, nouvellesPortions));

        return mapToResponse(updatedRecette);
    }

    private RecetteResponse mapToResponse(Recette recette) {
        List<LigneRecetteResponse> lignes = recette.getLignesRecette().stream()
                .map(ligne -> LigneRecetteResponse.builder()
                        .id(ligne.getId())
                        .ingredientId(ligne.getIngredient().getId())
                        .ingredientNom(ligne.getIngredient().getNom())
                        .uniteMesure(ligne.getIngredient().getUniteMesure())
                        .quantite(ligne.getQuantite())
                        .instructionsSpecifiques(ligne.getInstructionsSpecifiques())
                        .coutIngredient(ligne.getIngredient().getPrixUnitaire() != null && ligne.getQuantite() != null ?
                                ligne.getIngredient().getPrixUnitaire().multiply(ligne.getQuantite()) : BigDecimal.ZERO)
                        .build())
                .toList();

        return RecetteResponse.builder()
                .id(recette.getId())
                .nom(recette.getNom())
                .description(recette.getDescription())
                .instructions(recette.getInstructions())
                .tempsPreparation(recette.getTempsPreparation())
                .tempsCuisson(recette.getTempsCuisson())
                .nombrePortions(recette.getNombrePortions())
                .coutTotal(recette.getCoutTotal())
                .coutParPortion(recette.getCoutParPortion())
                .createurId(recette.getCreateur() != null ? recette.getCreateur().getId() : null)
                .createurNom(recette.getCreateur() != null ?
                        recette.getCreateur().getNom() + " " + recette.getCreateur().getPrenom() : null)
                .dateCreation(recette.getDateCreation())
                .dateModification(recette.getDateModification())
                .lignesRecette(lignes)
                .build();
    }

}