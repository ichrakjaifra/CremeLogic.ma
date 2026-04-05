package ma.cremelogic.CremeLogic.ma.service.impl;

import ma.cremelogic.CremeLogic.ma.dto.request.IngredientRequest;
import ma.cremelogic.CremeLogic.ma.dto.response.IngredientResponse;
import ma.cremelogic.CremeLogic.ma.entity.Ingredient;
import ma.cremelogic.CremeLogic.ma.entity.Fournisseur;
import ma.cremelogic.CremeLogic.ma.enums.UniteMesure;
import ma.cremelogic.CremeLogic.ma.exception.ResourceNotFoundException;
import ma.cremelogic.CremeLogic.ma.exception.ValidationException;
import ma.cremelogic.CremeLogic.ma.repository.IngredientRepository;
import ma.cremelogic.CremeLogic.ma.repository.FournisseurRepository;
import ma.cremelogic.CremeLogic.ma.repository.MouvementStockRepository;
import ma.cremelogic.CremeLogic.ma.service.interfaces.IngredientService;
import ma.cremelogic.CremeLogic.ma.service.interfaces.AlerteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class IngredientServiceImpl implements IngredientService {

    private final IngredientRepository ingredientRepository;
    private final FournisseurRepository fournisseurRepository;
    private final MouvementStockRepository mouvementStockRepository;
    private final AlerteService alerteService;
    private final HistoriqueService historiqueService;
    private final ma.cremelogic.CremeLogic.ma.repository.UtilisateurRepository utilisateurRepository;
    private final ma.cremelogic.CremeLogic.ma.repository.AlerteRepository alerteRepository;

    @Override
    @Transactional
    public IngredientResponse createIngredient(IngredientRequest request) {
        // Validation
        if (request.getQuantiteMinimum().compareTo(request.getQuantiteMaximum()) > 0) {
            throw new ValidationException("La quantité minimum ne peut pas être supérieure à la quantité maximum");
        }

        // Vérifier si le code ingrédient existe déjà
        String codeIngredient = generateCodeIngredient(request.getNom());
        if (ingredientRepository.existsByCodeIngredient(codeIngredient)) {
            throw new ValidationException("Un ingrédient avec un code similaire existe déjà");
        }

        // Créer l'ingrédient
        Ingredient ingredient = Ingredient.builder()
                .nom(request.getNom())
                .codeIngredient(codeIngredient)
                .description(request.getDescription())
                .uniteMesure(request.getUniteMesure())
                .quantiteStock(BigDecimal.ZERO)
                .quantiteMinimum(request.getQuantiteMinimum())
                .quantiteMaximum(request.getQuantiteMaximum())
                .prixUnitaire(request.getPrixUnitaire())
                .perissable(request.isPerissable())
                .dateExpiration(request.getDateExpiration())
                .build();

        // Lier le fournisseur si fourni
        if (request.getFournisseurPrincipalId() != null) {
            Fournisseur fournisseur = fournisseurRepository.findById(request.getFournisseurPrincipalId())
                    .orElseThrow(() -> new ResourceNotFoundException("Fournisseur", "id",
                            request.getFournisseurPrincipalId()));
            ingredient.setFournisseurPrincipal(fournisseur);
        }

        Ingredient savedIngredient = ingredientRepository.save(ingredient);

        // Historique
        historiqueService.enregistrerCreation("INGREDIENT", savedIngredient.getId(),
                "Création de l'ingrédient: " + savedIngredient.getNom());

        log.info("Ingrédient créé: {}", savedIngredient.getCodeIngredient());
        return mapToResponse(savedIngredient);
    }

    @Override
    @Transactional
    public IngredientResponse updateIngredient(Long id, IngredientRequest request) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ingrédient", "id", id));

        // Validation
        if (request.getQuantiteMinimum().compareTo(request.getQuantiteMaximum()) > 0) {
            throw new ValidationException("La quantité minimum ne peut pas être supérieure à la quantité maximum");
        }

        // Sauvegarder anciennes valeurs pour historique
        String ancienNom = ingredient.getNom();
        BigDecimal ancienPrix = ingredient.getPrixUnitaire();

        // Mettre à jour
        ingredient.setNom(request.getNom());
        ingredient.setDescription(request.getDescription());
        ingredient.setUniteMesure(request.getUniteMesure());
        ingredient.setQuantiteMinimum(request.getQuantiteMinimum());
        ingredient.setQuantiteMaximum(request.getQuantiteMaximum());
        ingredient.setPrixUnitaire(request.getPrixUnitaire());
        ingredient.setPerissable(request.isPerissable());
        ingredient.setDateExpiration(request.getDateExpiration());

        // Mettre à jour le fournisseur si fourni
        if (request.getFournisseurPrincipalId() != null) {
            Fournisseur fournisseur = fournisseurRepository.findById(request.getFournisseurPrincipalId())
                    .orElseThrow(() -> new ResourceNotFoundException("Fournisseur", "id",
                            request.getFournisseurPrincipalId()));
            ingredient.setFournisseurPrincipal(fournisseur);
        } else if (ingredient.getFournisseurPrincipal() != null) {
            ingredient.setFournisseurPrincipal(null);
        }

        Ingredient updatedIngredient = ingredientRepository.save(ingredient);

        // Vérifier les alertes
        verifierAlertesIngredient(id);

        // Historique
        historiqueService.enregistrerModification("INGREDIENT", id,
                String.format("Mise à jour: %s -> %s, Prix: %s -> %s",
                        ancienNom, ingredient.getNom(), ancienPrix, ingredient.getPrixUnitaire()));

        log.info("Ingrédient mis à jour: {}", ingredient.getCodeIngredient());
        return mapToResponse(updatedIngredient);
    }

    @Override
    public IngredientResponse getIngredient(Long id) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ingrédient", "id", id));
        return mapToResponse(ingredient);
    }

    @Override
    public List<IngredientResponse> getAllIngredients() {
        return ingredientRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<IngredientResponse> getIngredientsStockFaible() {
        return ingredientRepository.findIngredientsStockFaible().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<IngredientResponse> getIngredientsExpirant() {
        LocalDate dateLimite = LocalDate.now().plusDays(7);
        return ingredientRepository.findIngredientsExpirantAvant(dateLimite).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<IngredientResponse> searchIngredients(String keyword) {
        return ingredientRepository.findByNomContainingIgnoreCase(keyword).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public void deleteIngredient(Long id) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ingrédient", "id", id));

        // Vérifier si l'ingrédient est utilisé dans des recettes
        if (!ingredient.getLignesRecette().isEmpty()) {
            throw new ValidationException("Impossible de supprimer un ingrédient utilisé dans des recettes");
        }

        // Supprimer les alertes liées (FK constraint)
        alerteRepository.deleteByIngredientId(id);

        // Supprimer les mouvements de stock liés (FK constraint)
        mouvementStockRepository.deleteByIngredientId(id);

        ingredientRepository.delete(ingredient);
        historiqueService.enregistrerSuppression("INGREDIENT", id,
                "Suppression de l'ingrédient: " + ingredient.getNom());
        log.info("Ingrédient supprimé: {}", ingredient.getCodeIngredient());
    }

    @Override
    @Transactional
    public IngredientResponse ajusterStock(Long id, BigDecimal quantite, String type, String raison) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ingrédient", "id", id));

        BigDecimal ancienneQuantite = ingredient.getQuantiteStock();
        BigDecimal nouvelleQuantite;

        switch (type.toUpperCase()) {
            case "ENTREE" -> {
                nouvelleQuantite = ancienneQuantite.add(quantite);
                ingredient.setQuantiteStock(nouvelleQuantite);

                // Créer un mouvement de stock
                createMouvementStock(ingredient, "ENTREE", quantite, raison);
            }
            case "SORTIE" -> {
                if (quantite.compareTo(ancienneQuantite) > 0) {
                    throw new ValidationException("Stock insuffisant. Disponible: " + ancienneQuantite);
                }
                nouvelleQuantite = ancienneQuantite.subtract(quantite);
                ingredient.setQuantiteStock(nouvelleQuantite);

                // Créer un mouvement de stock
                createMouvementStock(ingredient, "SORTIE", quantite, raison);
            }
            case "AJUSTEMENT" -> {
                nouvelleQuantite = quantite;
                ingredient.setQuantiteStock(nouvelleQuantite);

                // Créer un mouvement de stock
                createMouvementStock(ingredient, "AJUSTEMENT",
                        nouvelleQuantite.subtract(ancienneQuantite), raison);
            }
            case "PERDU", "DETRUIT" -> {
                if (quantite.compareTo(ancienneQuantite) > 0) {
                    quantite = ancienneQuantite; // Ne peut pas perdre plus que le stock disponible
                }
                nouvelleQuantite = ancienneQuantite.subtract(quantite);
                ingredient.setQuantiteStock(nouvelleQuantite);

                // Créer un mouvement de stock
                createMouvementStock(ingredient, type.toUpperCase(), quantite, raison);
            }
            default -> throw new ValidationException("Type d'ajustement invalide: " + type);
        }

        Ingredient updatedIngredient = ingredientRepository.save(ingredient);

        // Vérifier les alertes
        verifierAlertesIngredient(id);

        // Historique
        historiqueService.enregistrerModification("INGREDIENT", id,
                String.format("%s stock: %s -> %s (%s %s)",
                        type, ancienneQuantite, nouvelleQuantite, quantite, ingredient.getUniteMesure().getSymbole()));

        return mapToResponse(updatedIngredient);
    }

    @Override
    public BigDecimal getConsommationMoyenne(Long ingredientId, int jours) {
        LocalDateTime dateDebut = LocalDateTime.now().minusDays(jours);
        LocalDateTime dateFin = LocalDateTime.now();

        Double totalSorties = mouvementStockRepository.getTotalSorties(ingredientId, dateDebut, dateFin);

        if (totalSorties == null) {
            return BigDecimal.ZERO;
        }

        return BigDecimal.valueOf(totalSorties / jours);
    }

    @Override
    public BigDecimal getValeurStockTotal() {
        BigDecimal valeur = ingredientRepository.getValeurStockTotal();
        return valeur != null ? valeur : BigDecimal.ZERO;
    }

    @Override
    public List<IngredientResponse> getIngredientsParFournisseur(Long fournisseurId) {
        return ingredientRepository.findAll().stream()
                .filter(i -> i.getFournisseurPrincipal() != null &&
                        i.getFournisseurPrincipal().getId().equals(fournisseurId))
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public void verifierAlertesIngredient(Long ingredientId) {
        Ingredient ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new ResourceNotFoundException("Ingrédient", "id", ingredientId));

        // Vérifier stock faible
        if (ingredient.estStockFaible()) {
            alerteService.creerAlerteStockFaible(ingredient);
        }

        // Vérifier rupture de stock
        if (ingredient.estEnRupture()) {
            alerteService.creerAlerteRuptureStock(ingredient);
        }

        // Vérifier expiration
        if (ingredient.estExpire()) {
            alerteService.creerAlerteExpiration(ingredient);
        }
    }

    // Méthodes privées
    private String generateCodeIngredient(String nom) {
        String code = "ING-" + nom.substring(0, Math.min(3, nom.length())).toUpperCase() +
                "-" + System.currentTimeMillis() % 10000;
        return code;
    }

    private void createMouvementStock(Ingredient ingredient, String type, BigDecimal quantite, String raison) {
        ma.cremelogic.CremeLogic.ma.enums.TypeMouvement typeMouvement = ma.cremelogic.CremeLogic.ma.enums.TypeMouvement.valueOf(type);
        ma.cremelogic.CremeLogic.ma.entity.Utilisateur utilisateur = getCurrentUser();
        
        BigDecimal ancienneQuantite = ingredient.getQuantiteStock();
        // Since the caller already updated ingredient.getQuantiteStock(), we need to approximate ancienneQuantite based on type.
        if (typeMouvement == ma.cremelogic.CremeLogic.ma.enums.TypeMouvement.ENTREE) {
             ancienneQuantite = ingredient.getQuantiteStock().subtract(quantite);
        } else if (typeMouvement == ma.cremelogic.CremeLogic.ma.enums.TypeMouvement.SORTIE || typeMouvement == ma.cremelogic.CremeLogic.ma.enums.TypeMouvement.PERDU) {
             ancienneQuantite = ingredient.getQuantiteStock().add(quantite);
        } else if (typeMouvement == ma.cremelogic.CremeLogic.ma.enums.TypeMouvement.AJUSTEMENT) {
             ancienneQuantite = ancienneQuantite.subtract(quantite); // quantite here is the difference
        }

        ma.cremelogic.CremeLogic.ma.entity.MouvementStock mouvement = ma.cremelogic.CremeLogic.ma.entity.MouvementStock.builder()
                .ingredient(ingredient)
                .type(typeMouvement)
                .quantite(quantite)
                .quantiteAvant(ancienneQuantite)
                .quantiteApres(ingredient.getQuantiteStock())
                .coutUnitaire(ingredient.getPrixUnitaire())
                .montantTotal(ingredient.getPrixUnitaire() != null ? ingredient.getPrixUnitaire().multiply(quantite) : null)
                .utilisateur(utilisateur)
                .raison(raison)
                .build();
        
        mouvementStockRepository.save(mouvement);
        
        log.info("Mouvement de stock enregistré: {} {} {} ({})",
                type, quantite, ingredient.getUniteMesure().getSymbole(), raison);
    }

    private ma.cremelogic.CremeLogic.ma.entity.Utilisateur getCurrentUser() {
        try {
            String email = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
            return utilisateurRepository.findByEmail(email).orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    private IngredientResponse mapToResponse(Ingredient ingredient) {
        return IngredientResponse.builder()
                .id(ingredient.getId())
                .codeIngredient(ingredient.getCodeIngredient())
                .nom(ingredient.getNom())
                .description(ingredient.getDescription())
                .uniteMesure(ingredient.getUniteMesure())
                .quantiteStock(ingredient.getQuantiteStock())
                .quantiteMinimum(ingredient.getQuantiteMinimum())
                .quantiteMaximum(ingredient.getQuantiteMaximum())
                .prixUnitaire(ingredient.getPrixUnitaire())
                .perissable(ingredient.isPerissable())
                .dateExpiration(ingredient.getDateExpiration())
                .fournisseurPrincipalId(
                        ingredient.getFournisseurPrincipal() != null ? ingredient.getFournisseurPrincipal().getId()
                                : null)
                .fournisseurNom(
                        ingredient.getFournisseurPrincipal() != null ? ingredient.getFournisseurPrincipal().getNom()
                                : null)
                .dateCreation(ingredient.getDateCreation())
                .dateModification(ingredient.getDateModification())
                .stockFaible(ingredient.estStockFaible())
                .enRupture(ingredient.estEnRupture())
                .expire(ingredient.estExpire())
                .valeurStock(ingredient.getPrixUnitaire() != null && ingredient.getQuantiteStock() != null
                        ? ingredient.getPrixUnitaire().multiply(ingredient.getQuantiteStock())
                        : BigDecimal.ZERO)
                .consommationMoyenne(getConsommationMoyenne(ingredient.getId(), 30))
                .build();
    }
}