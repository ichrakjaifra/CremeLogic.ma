package ma.cremelogic.CremeLogic.ma.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.cremelogic.CremeLogic.ma.dto.response.MouvementStockResponse;
import ma.cremelogic.CremeLogic.ma.entity.*;
import ma.cremelogic.CremeLogic.ma.enums.TypeMouvement;
import ma.cremelogic.CremeLogic.ma.exception.ResourceNotFoundException;
import ma.cremelogic.CremeLogic.ma.exception.ValidationException;
import ma.cremelogic.CremeLogic.ma.repository.*;
import ma.cremelogic.CremeLogic.ma.service.interfaces.AlerteService;
import ma.cremelogic.CremeLogic.ma.service.interfaces.MouvementStockService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MouvementStockServiceImpl implements MouvementStockService {

    private final MouvementStockRepository mouvementStockRepository;
    private final IngredientRepository ingredientRepository;
    private final CommandeAchatRepository commandeAchatRepository;
    private final OrdreProductionRepository ordreProductionRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final AlerteService alerteService;
    private final HistoriqueService historiqueService;

    @Override
    @Transactional
    public MouvementStockResponse enregistrerEntree(Long ingredientId, BigDecimal quantite, BigDecimal coutUnitaire, String raison, Long commandeId) {
        Ingredient ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new ResourceNotFoundException("Ingrédient", "id", ingredientId));

        Utilisateur utilisateur = getCurrentUser();
        BigDecimal ancienneQuantite = ingredient.getQuantiteStock();
        BigDecimal nouvelleQuantite = ancienneQuantite.add(quantite);

        // Mettre à jour le stock
        ingredient.setQuantiteStock(nouvelleQuantite);
        if (coutUnitaire != null) {
            ingredient.setPrixUnitaire(coutUnitaire);
        }
        ingredientRepository.save(ingredient);

        // Créer le mouvement
        MouvementStock mouvement = MouvementStock.builder()
                .ingredient(ingredient)
                .type(TypeMouvement.ENTREE)
                .quantite(quantite)
                .quantiteAvant(ancienneQuantite)
                .quantiteApres(nouvelleQuantite)
                .coutUnitaire(coutUnitaire)
                .montantTotal(coutUnitaire != null ? coutUnitaire.multiply(quantite) : null)
                .utilisateur(utilisateur)
                .raison(raison)
                .build();

        if (commandeId != null) {
            CommandeAchat commande = commandeAchatRepository.findById(commandeId)
                    .orElseThrow(() -> new ResourceNotFoundException("Commande", "id", commandeId));
            mouvement.setCommande(commande);
        }

        MouvementStock saved = mouvementStockRepository.save(mouvement);

        // Historique
        historiqueService.enregistrerModification("INGREDIENT", ingredientId,
                String.format("Entrée stock: %s %s", quantite, ingredient.getUniteMesure().getSymbole()));

        // Vérifier les alertes
        alerteService.verifierAlertesIngredient(ingredientId);

        log.info("Entrée stock enregistrée: {} {} pour {}", quantite, ingredient.getUniteMesure(), ingredient.getNom());
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public MouvementStockResponse enregistrerSortie(Long ingredientId, BigDecimal quantite, String raison, Long ordreProductionId) {
        Ingredient ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new ResourceNotFoundException("Ingrédient", "id", ingredientId));

        if (ingredient.getQuantiteStock().compareTo(quantite) < 0) {
            throw new ValidationException("Stock insuffisant. Disponible: " + ingredient.getQuantiteStock() +
                    " " + ingredient.getUniteMesure().getSymbole());
        }

        Utilisateur utilisateur = getCurrentUser();
        BigDecimal ancienneQuantite = ingredient.getQuantiteStock();
        BigDecimal nouvelleQuantite = ancienneQuantite.subtract(quantite);

        // Mettre à jour le stock
        ingredient.setQuantiteStock(nouvelleQuantite);
        ingredientRepository.save(ingredient);

        // Créer le mouvement
        MouvementStock mouvement = MouvementStock.builder()
                .ingredient(ingredient)
                .type(TypeMouvement.SORTIE)
                .quantite(quantite)
                .quantiteAvant(ancienneQuantite)
                .quantiteApres(nouvelleQuantite)
                .coutUnitaire(ingredient.getPrixUnitaire())
                .montantTotal(ingredient.getPrixUnitaire() != null ? ingredient.getPrixUnitaire().multiply(quantite) : null)
                .utilisateur(utilisateur)
                .raison(raison)
                .build();

        if (ordreProductionId != null) {
            OrdreProduction ordre = ordreProductionRepository.findById(ordreProductionId)
                    .orElseThrow(() -> new ResourceNotFoundException("Ordre de production", "id", ordreProductionId));
            mouvement.setOrdreProduction(ordre);
        }

        MouvementStock saved = mouvementStockRepository.save(mouvement);

        // Historique
        historiqueService.enregistrerModification("INGREDIENT", ingredientId,
                String.format("Sortie stock: %s %s", quantite, ingredient.getUniteMesure().getSymbole()));

        // Vérifier les alertes
        alerteService.verifierAlertesIngredient(ingredientId);

        log.info("Sortie stock enregistrée: {} {} pour {}", quantite, ingredient.getUniteMesure(), ingredient.getNom());
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public MouvementStockResponse enregistrerPerte(Long ingredientId, BigDecimal quantite, String raison) {
        Ingredient ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new ResourceNotFoundException("Ingrédient", "id", ingredientId));

        if (ingredient.getQuantiteStock().compareTo(quantite) < 0) {
            quantite = ingredient.getQuantiteStock(); // Ne peut pas perdre plus que le stock disponible
        }

        Utilisateur utilisateur = getCurrentUser();
        BigDecimal ancienneQuantite = ingredient.getQuantiteStock();
        BigDecimal nouvelleQuantite = ancienneQuantite.subtract(quantite);

        ingredient.setQuantiteStock(nouvelleQuantite);
        ingredientRepository.save(ingredient);

        MouvementStock mouvement = MouvementStock.builder()
                .ingredient(ingredient)
                .type(TypeMouvement.PERDU)
                .quantite(quantite)
                .quantiteAvant(ancienneQuantite)
                .quantiteApres(nouvelleQuantite)
                .coutUnitaire(ingredient.getPrixUnitaire())
                .montantTotal(ingredient.getPrixUnitaire() != null ? ingredient.getPrixUnitaire().multiply(quantite) : null)
                .utilisateur(utilisateur)
                .raison(raison != null ? raison : "Perte non spécifiée")
                .build();

        MouvementStock saved = mouvementStockRepository.save(mouvement);

        // Créer une alerte pour la perte
        alerteService.creerAlertePerteStock(ingredient, quantite, raison);

        log.warn("Perte enregistrée: {} {} pour {} - Raison: {}", quantite, ingredient.getUniteMesure(), ingredient.getNom(), raison);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public MouvementStockResponse enregistrerAjustement(Long ingredientId, BigDecimal nouvelleQuantite, String raison) {
        Ingredient ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new ResourceNotFoundException("Ingrédient", "id", ingredientId));

        Utilisateur utilisateur = getCurrentUser();
        BigDecimal ancienneQuantite = ingredient.getQuantiteStock();
        BigDecimal difference = nouvelleQuantite.subtract(ancienneQuantite);

        ingredient.setQuantiteStock(nouvelleQuantite);
        ingredientRepository.save(ingredient);

        MouvementStock mouvement = MouvementStock.builder()
                .ingredient(ingredient)
                .type(TypeMouvement.AJUSTEMENT)
                .quantite(difference)
                .quantiteAvant(ancienneQuantite)
                .quantiteApres(nouvelleQuantite)
                .coutUnitaire(ingredient.getPrixUnitaire())
                .utilisateur(utilisateur)
                .raison(raison != null ? raison : "Ajustement de stock")
                .build();

        MouvementStock saved = mouvementStockRepository.save(mouvement);

        // Historique
        historiqueService.enregistrerModification("INGREDIENT", ingredientId,
                String.format("Ajustement stock: %s %s -> %s %s (Diff: %s)",
                        ancienneQuantite, ingredient.getUniteMesure().getSymbole(),
                        nouvelleQuantite, ingredient.getUniteMesure().getSymbole(),
                        difference));

        // Vérifier les alertes
        alerteService.verifierAlertesIngredient(ingredientId);

        log.info("Ajustement stock: {} -> {} pour {}", ancienneQuantite, nouvelleQuantite, ingredient.getNom());
        return mapToResponse(saved);
    }

    @Override
    public List<MouvementStockResponse> getMouvementsParIngredient(Long ingredientId) {
        return mouvementStockRepository.findByIngredientIdOrderByDateMouvementDesc(ingredientId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<MouvementStockResponse> getMouvementsParPeriode(LocalDateTime debut, LocalDateTime fin) {
        return mouvementStockRepository.findByDateMouvementBetweenOrderByDateMouvementDesc(debut, fin).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<MouvementStockResponse> getMouvementsParType(TypeMouvement type) {
        return mouvementStockRepository.findByTypeOrderByDateMouvementDesc(type).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, BigDecimal> getStatistiquesConsommation(Long ingredientId, LocalDateTime debut, LocalDateTime fin) {
        Map<String, BigDecimal> stats = new HashMap<>();

        Double totalEntrees = mouvementStockRepository.getTotalEntrees(ingredientId, debut, fin);
        Double totalSorties = mouvementStockRepository.getTotalSorties(ingredientId, debut, fin);
        Double totalPertes = mouvementStockRepository.getTotalPertes(ingredientId, debut, fin);

        stats.put("totalEntrees", totalEntrees != null ? BigDecimal.valueOf(totalEntrees) : BigDecimal.ZERO);
        stats.put("totalSorties", totalSorties != null ? BigDecimal.valueOf(totalSorties) : BigDecimal.ZERO);
        stats.put("totalPertes", totalPertes != null ? BigDecimal.valueOf(totalPertes) : BigDecimal.ZERO);

        // Consommation moyenne par jour
        long jours = java.time.Duration.between(debut, fin).toDays();
        if (jours > 0 && totalSorties != null) {
            stats.put("consommationMoyenneJour", BigDecimal.valueOf(totalSorties / jours));
        }

        return stats;
    }

    @Override
    public Map<String, Object> getHistoriqueComplet(Long ingredientId, LocalDateTime debut, LocalDateTime fin) {
        Map<String, Object> result = new HashMap<>();

        List<MouvementStock> mouvements = mouvementStockRepository
                .findByIngredientIdAndDateMouvementBetweenOrderByDateMouvementDesc(ingredientId, debut, fin);

        result.put("mouvements", mouvements.stream().map(this::mapToResponse).collect(Collectors.toList()));
        result.put("statistiques", getStatistiquesConsommation(ingredientId, debut, fin));

        Ingredient ingredient = ingredientRepository.findById(ingredientId).orElse(null);
        if (ingredient != null) {
            result.put("stockActuel", ingredient.getQuantiteStock());
            result.put("stockMinimum", ingredient.getQuantiteMinimum());
            result.put("stockMaximum", ingredient.getQuantiteMaximum());
        }

        return result;
    }

    @Override
    @Transactional
    public void verifierEtCreerAlertes() {
        List<Ingredient> ingredients = ingredientRepository.findAll();

        for (Ingredient ingredient : ingredients) {
            // Vérifier stock faible
            if (ingredient.estStockFaible()) {
                alerteService.creerAlerteStockFaible(ingredient);
            }

            // Vérifier expiration
            if (ingredient.estExpire()) {
                alerteService.creerAlerteExpiration(ingredient);
            }
        }

        log.info("Vérification automatique des alertes terminée");
    }

    @Override
    @Transactional
    public void deduireConsommationProduction(Long ordreProductionId, Map<Long, BigDecimal> consommations) {
        OrdreProduction ordre = ordreProductionRepository.findById(ordreProductionId)
                .orElseThrow(() -> new ResourceNotFoundException("Ordre de production", "id", ordreProductionId));

        String raison = "Consommation pour production: " + ordre.getNumeroOrdre();

        for (Map.Entry<Long, BigDecimal> consommation : consommations.entrySet()) {
            enregistrerSortie(consommation.getKey(), consommation.getValue(), raison, ordreProductionId);
        }

        log.info("Consommations déduites pour l'ordre de production {}", ordre.getNumeroOrdre());
    }

    private Utilisateur getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "email", email));
    }

    private MouvementStockResponse mapToResponse(MouvementStock mouvement) {
        return MouvementStockResponse.builder()
                .id(mouvement.getId())
                .dateMouvement(mouvement.getDateMouvement())
                .ingredientId(mouvement.getIngredient().getId())
                .ingredientNom(mouvement.getIngredient().getNom())
                .type(mouvement.getType())
                .quantite(mouvement.getQuantite())
                .quantiteAvant(mouvement.getQuantiteAvant())
                .quantiteApres(mouvement.getQuantiteApres())
                .coutUnitaire(mouvement.getCoutUnitaire())
                .montantTotal(mouvement.getMontantTotal())
                .utilisateurId(mouvement.getUtilisateur() != null ? mouvement.getUtilisateur().getId() : null)
                .utilisateurNom(mouvement.getUtilisateur() != null ?
                        mouvement.getUtilisateur().getNom() + " " + mouvement.getUtilisateur().getPrenom() : null)
                .commandeId(mouvement.getCommande() != null ? mouvement.getCommande().getId() : null)
                .ordreProductionId(mouvement.getOrdreProduction() != null ? mouvement.getOrdreProduction().getId() : null)
                .venteId(mouvement.getVente() != null ? mouvement.getVente().getId() : null)
                .raison(mouvement.getRaison())
                .synchronise(mouvement.isSynchronise())
                .build();
    }
}