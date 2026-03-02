package ma.cremelogic.CremeLogic.ma.service.impl;

import ma.cremelogic.CremeLogic.ma.dto.response.AlerteResponse;
import ma.cremelogic.CremeLogic.ma.entity.Alerte;
import ma.cremelogic.CremeLogic.ma.entity.Ingredient;
import ma.cremelogic.CremeLogic.ma.entity.Produit;
import ma.cremelogic.CremeLogic.ma.entity.CommandeAchat;
import ma.cremelogic.CremeLogic.ma.entity.OrdreProduction;
import ma.cremelogic.CremeLogic.ma.enums.TypeAlerte;
import ma.cremelogic.CremeLogic.ma.repository.AlerteRepository;
import ma.cremelogic.CremeLogic.ma.repository.IngredientRepository;
import ma.cremelogic.CremeLogic.ma.service.interfaces.AlerteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlerteServiceImpl implements AlerteService {

    private final AlerteRepository alerteRepository;
    private final IngredientRepository ingredientRepository;

    @Override
    @Transactional
    public void creerAlerteStockFaible(Ingredient ingredient) {
        // Vérifier si une alerte similaire existe déjà
        boolean existeDeja = alerteRepository.findAll().stream()
                .anyMatch(a -> !a.isResolue() &&
                        a.getType() == TypeAlerte.STOCK_FAIBLE &&
                        a.getIngredient() != null &&
                        a.getIngredient().getId().equals(ingredient.getId()));

        if (!existeDeja) {
            Alerte alerte = Alerte.builder()
                    .type(TypeAlerte.STOCK_FAIBLE)
                    .titre("Stock faible - " + ingredient.getNom())
                    .description(String.format("Le stock de %s est faible (%.2f %s). Quantité minimum: %.2f %s",
                            ingredient.getNom(),
                            ingredient.getQuantiteStock(),
                            ingredient.getUniteMesure().getSymbole(),
                            ingredient.getQuantiteMinimum(),
                            ingredient.getUniteMesure().getSymbole()))
                    .priorite("HAUTE")
                    .resolue(false)
                    .ingredient(ingredient)
                    .build();

            alerteRepository.save(alerte);
            log.info("Alerte stock faible créée pour: {}", ingredient.getNom());
        }
    }

    @Override
    @Transactional
    public void creerAlerteRuptureStock(Ingredient ingredient) {
        boolean existeDeja = alerteRepository.findAll().stream()
                .anyMatch(a -> !a.isResolue() &&
                        a.getType() == TypeAlerte.STOCK_FAIBLE &&
                        a.getIngredient() != null &&
                        a.getIngredient().getId().equals(ingredient.getId()));

        if (!existeDeja) {
            Alerte alerte = Alerte.builder()
                    .type(TypeAlerte.STOCK_FAIBLE)
                    .titre("Rupture de stock - " + ingredient.getNom())
                    .description(String.format("Le stock de %s est épuisé. Quantité disponible: %.2f %s",
                            ingredient.getNom(),
                            ingredient.getQuantiteStock(),
                            ingredient.getUniteMesure().getSymbole()))
                    .priorite("HAUTE")
                    .resolue(false)
                    .ingredient(ingredient)
                    .build();

            alerteRepository.save(alerte);
            log.warn("Alerte rupture de stock créée pour: {}", ingredient.getNom());
        }
    }

    @Override
    @Transactional
    public void creerAlerteExpiration(Ingredient ingredient) {
        Alerte alerte = Alerte.builder()
                .type(TypeAlerte.DATE_EXPIRATION)
                .titre("Ingrédient expiré - " + ingredient.getNom())
                .description(String.format("L'ingrédient %s a expiré le %s. Quantité: %.2f %s",
                        ingredient.getNom(),
                        ingredient.getDateExpiration(),
                        ingredient.getQuantiteStock(),
                        ingredient.getUniteMesure().getSymbole()))
                .priorite("MOYENNE")
                .resolue(false)
                .ingredient(ingredient)
                .build();

        alerteRepository.save(alerte);
        log.warn("Alerte expiration créée pour: {}", ingredient.getNom());
    }

    @Override
    @Transactional
    public void creerAlerteCommandeRetard(CommandeAchat commande) {
        Alerte alerte = Alerte.builder()
                .type(TypeAlerte.COMMANDE_RETARD)
                .titre("Commande en retard - " + commande.getNumeroCommande())
                .description(String.format("La commande %s auprès de %s est en retard. Date de livraison prévue: %s",
                        commande.getNumeroCommande(),
                        commande.getFournisseur().getNom(),
                        commande.getDateLivraisonPrevue()))
                .priorite("MOYENNE")
                .resolue(false)
                .commande(commande)
                .build();

        alerteRepository.save(alerte);
        log.warn("Alerte commande en retard créée pour: {}", commande.getNumeroCommande());
    }

    @Override
    @Transactional
    public void creerAlerteProductionRetard(OrdreProduction ordre) {
        Alerte alerte = Alerte.builder()
                .type(TypeAlerte.PRODUCTION_RETARD)
                .titre("Production en retard - " + ordre.getNumeroOrdre())
                .description(String.format("L'ordre de production %s pour %s est en retard. Date de fin prévue: %s",
                        ordre.getNumeroOrdre(),
                        ordre.getProduit().getNom(),
                        ordre.getDateFinPrevue()))
                .priorite("MOYENNE")
                .resolue(false)
                .ordreProduction(ordre)
                .build();

        alerteRepository.save(alerte);
        log.warn("Alerte production en retard créée pour: {}", ordre.getNumeroOrdre());
    }

    @Override
    public List<AlerteResponse> getAlertesNonResolues() {
        return alerteRepository.findAlertesNonResoluesTriees().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<AlerteResponse> getAlertesParType(TypeAlerte type) {
        return alerteRepository.findByType(type).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public AlerteResponse resoudreAlerte(Long id, String commentaire) {
        Alerte alerte = alerteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alerte non trouvée"));

        alerte.setResolue(true);
        alerte.setDateResolution(LocalDateTime.now());

        if (commentaire != null && !commentaire.isEmpty()) {
            alerte.setDescription(alerte.getDescription() + "\nRésolution: " + commentaire);
        }

        Alerte resolved = alerteRepository.save(alerte);
        log.info("Alerte résolue: {}", alerte.getTitre());

        return mapToResponse(resolved);
    }

    @Override
    @Transactional
    @Scheduled(cron = "0 0 6 * * ?") // Tous les jours à 6h du matin
    public void verifierAlertesAutomatiques() {
        log.info("Début de la vérification automatique des alertes");

        // Vérifier les ingrédients en stock faible
        List<Ingredient> ingredientsStockFaible = ingredientRepository.findIngredientsStockFaible();
        ingredientsStockFaible.forEach(this::creerAlerteStockFaible);

        // Vérifier les ingrédients expirés
        List<Ingredient> ingredientsExpires = ingredientRepository.findIngredientsExpires();
        ingredientsExpires.forEach(this::creerAlerteExpiration);

        // Vérifier les alertes expirées
        LocalDateTime dateLimite = LocalDateTime.now().minusDays(7);
        List<Alerte> alertesExpirees = alerteRepository.findAlertesNonResoluesExpirees(dateLimite);
        alertesExpirees.forEach(alerte -> {
            alerte.setDescription(alerte.getDescription() + " [ALERTE EXPIRÉE]");
            alerteRepository.save(alerte);
        });

        log.info("Fin de la vérification automatique des alertes. {} alertes créées/actualisées",
                ingredientsStockFaible.size() + ingredientsExpires.size());
    }

    private AlerteResponse mapToResponse(Alerte alerte) {
        return AlerteResponse.builder()
                .id(alerte.getId())
                .type(alerte.getType())
                .titre(alerte.getTitre())
                .description(alerte.getDescription())
                .dateCreation(alerte.getDateCreation())
                .dateResolution(alerte.getDateResolution())
                .resolue(alerte.isResolue())
                .priorite(alerte.getPriorite())
                .ingredientId(alerte.getIngredient() != null ? alerte.getIngredient().getId() : null)
                .produitId(alerte.getProduit() != null ? alerte.getProduit().getId() : null)
                .commandeId(alerte.getCommande() != null ? alerte.getCommande().getId() : null)
                .ordreProductionId(alerte.getOrdreProduction() != null ? alerte.getOrdreProduction().getId() : null)
                .utilisateurId(alerte.getUtilisateur() != null ? alerte.getUtilisateur().getId() : null)
                .build();
    }
}