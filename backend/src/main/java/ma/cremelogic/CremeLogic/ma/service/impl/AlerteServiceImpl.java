package ma.cremelogic.CremeLogic.ma.service.impl;

import ma.cremelogic.CremeLogic.ma.dto.response.AlerteResponse;
import ma.cremelogic.CremeLogic.ma.entity.*;
import ma.cremelogic.CremeLogic.ma.enums.TypeAlerte;
import ma.cremelogic.CremeLogic.ma.repository.*;
import ma.cremelogic.CremeLogic.ma.service.interfaces.AlerteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlerteServiceImpl implements AlerteService {

        private final AlerteRepository alerteRepository;
        private final IngredientRepository ingredientRepository;
        private final CommandeAchatRepository commandeAchatRepository;
        private final OrdreProductionRepository ordreProductionRepository;
        private final SimpMessagingTemplate messagingTemplate;

        private void envoyerNotification(Alerte alerte) {
                try {
                        AlerteResponse response = mapToResponse(alerte);
                        messagingTemplate.convertAndSend("/topic/notifications", response);
                        log.debug("Notification envoyée via WebSocket pour l'alerte: {}", alerte.getTitre());
                } catch (Exception e) {
                        log.error("Erreur lors de l'envoi de la notification WebSocket", e);
                }
        }

        private void sauvegarderEtNotifier(Alerte alerte) {
                Alerte saved = alerteRepository.save(alerte);
                envoyerNotification(saved);
        }

        @Override
        @Transactional
        public void creerAlerteStockFaible(Ingredient ingredient) {
                boolean existeDeja = alerteRepository.findAll().stream()
                                .anyMatch(a -> !a.isResolue() &&
                                                a.getType() == TypeAlerte.STOCK_FAIBLE &&
                                                a.getIngredient() != null &&
                                                a.getIngredient().getId().equals(ingredient.getId()));

                if (!existeDeja) {
                        Alerte alerte = Alerte.builder()
                                        .type(TypeAlerte.STOCK_FAIBLE)
                                        .titre("Stock faible - " + ingredient.getNom())
                                        .description(String.format(
                                                        "Le stock de %s est faible (%.2f %s). Quantité minimum: %.2f %s",
                                                        ingredient.getNom(), ingredient.getQuantiteStock(),
                                                        ingredient.getUniteMesure().getSymbole(),
                                                        ingredient.getQuantiteMinimum(),
                                                        ingredient.getUniteMesure().getSymbole()))
                                        .priorite("HAUTE")
                                        .resolue(false)
                                         .ingredient(ingredient)
                                         .build();
                         sauvegarderEtNotifier(alerte);
                         log.info("Alerte stock faible créée pour: {}", ingredient.getNom());
                 }
        }

        @Override
        @Transactional
        public void creerAlerteRuptureStock(Ingredient ingredient) {
                Alerte alerte = Alerte.builder()
                                .type(TypeAlerte.STOCK_FAIBLE)
                                .titre("Rupture de stock - " + ingredient.getNom())
                                .description(String.format("Le stock de %s est épuisé.", ingredient.getNom()))
                                .priorite("HAUTE")
                                .resolue(false)
                                 .ingredient(ingredient)
                                 .build();
                 sauvegarderEtNotifier(alerte);
                 log.warn("Alerte rupture de stock créée for: {}", ingredient.getNom());
         }

        @Override
        @Transactional
        public void creerAlerteExpiration(Ingredient ingredient) {
                Alerte alerte = Alerte.builder()
                                .type(TypeAlerte.DATE_EXPIRATION)
                                .titre("Ingrédient expiré - " + ingredient.getNom())
                                .description(String.format("L'ingrédient %s a expiré le %s.", ingredient.getNom(),
                                                ingredient.getDateExpiration()))
                                .priorite("MOYENNE")
                                .resolue(false)
                                 .ingredient(ingredient)
                                 .build();
                 sauvegarderEtNotifier(alerte);
                 log.warn("Alerte expiration créée for: {}", ingredient.getNom());
         }

        @Override
        @Transactional
        public void creerAlerteCommandeRetard(CommandeAchat commande) {
                Alerte alerte = Alerte.builder()
                                .type(TypeAlerte.COMMANDE_RETARD)
                                .titre("Commande en retard - " + commande.getNumeroCommande())
                                .description("Commande de " + commande.getFournisseur().getNom() + " prévue le "
                                                + commande.getDateLivraisonPrevue())
                                .priorite("MOYENNE")
                                .resolue(false)
                                .commande(commande)
                                .build();
                sauvegarderEtNotifier(alerte);
        }

        @Override
        @Transactional
        public void creerAlerteCommandeAnnulee(CommandeAchat commande, String raison) {
                Alerte alerte = Alerte.builder()
                                .type(TypeAlerte.COMMANDE_ANNULEE)
                                .titre("Commande annulée - " + commande.getNumeroCommande())
                                .description("Raison: " + raison)
                                .priorite("MOYENNE")
                                .resolue(false)
                                .commande(commande)
                                .build();
                sauvegarderEtNotifier(alerte);
        }

        @Override
        @Transactional
        public void creerAlerteProductionRetard(OrdreProduction ordre) {
                Alerte alerte = Alerte.builder()
                                .type(TypeAlerte.PRODUCTION_RETARD)
                                .titre("Production en retard - " + ordre.getNumeroOrdre())
                                .description("Ordre pour " + ordre.getProduit().getNom() + " prévu le "
                                                + ordre.getDateFinPrevue())
                                .priorite("MOYENNE")
                                .resolue(false)
                                .ordreProduction(ordre)
                                .build();
                sauvegarderEtNotifier(alerte);
        }

        @Override
        @Transactional
        public void creerAlerteProductionAnnulee(OrdreProduction ordre, String raison) {
                Alerte alerte = Alerte.builder()
                                .type(TypeAlerte.PRODUCTION_ANNULEE)
                                .titre("Production annulée - " + ordre.getNumeroOrdre())
                                .description("Raison: " + raison)
                                .priorite("MOYENNE")
                                .resolue(false)
                                .ordreProduction(ordre)
                                .build();
                sauvegarderEtNotifier(alerte);
        }

        @Override
        @Transactional
        public void creerAlerteVenteAnnulee(Vente vente, String raison) {
                Alerte alerte = Alerte.builder()
                                .type(TypeAlerte.VENTE_ANNULEE)
                                .titre("Vente annulée - " + vente.getNumeroVente())
                                .description("Raison: " + raison)
                                .priorite("MOYENNE")
                                 .resolue(false)
                                 .vente(vente)
                                 .build();
                 sauvegarderEtNotifier(alerte);
         }

        @Override
        @Transactional
        public void creerAlertePerteStock(Ingredient ingredient, BigDecimal quantite, String raison) {
                Alerte alerte = Alerte.builder()
                                .type(TypeAlerte.PERTE_STOCK)
                                .titre("Perte stock - " + ingredient.getNom())
                                .description(String.format("Quantité: %s. Raison: %s", quantite, raison))
                                .priorite("HAUTE")
                                .resolue(false)
                                .ingredient(ingredient)
                                .build();
                sauvegarderEtNotifier(alerte);
        }

        @Override
        @Transactional
        public void creerAlerteIngredientsInsuffisants(OrdreProduction ordre, List<String> ingredientsManquants) {
                Alerte alerte = Alerte.builder()
                                .type(TypeAlerte.STOCK_FAIBLE)
                                .titre("Ingrédients insuffisants - " + ordre.getNumeroOrdre())
                                .description("Manquants: " + String.join(", ", ingredientsManquants))
                                .priorite("HAUTE")
                                .resolue(false)
                                .ordreProduction(ordre)
                                .build();
                sauvegarderEtNotifier(alerte);
        }

        @Override
        @Transactional
        public void creerAlertePersonnalisee(String titre, String description, String priorite, TypeAlerte type) {
                Alerte alerte = Alerte.builder()
                                .type(type)
                                .titre(titre)
                                .description(description)
                                 .priorite(priorite)
                                 .resolue(false)
                                 .build();
                 sauvegarderEtNotifier(alerte);
         }

         @Override
         @Transactional
         public void creerAlerteProductionTerminee(OrdreProduction ordre) {
                 Alerte alerte = Alerte.builder()
                                 .type(TypeAlerte.PRODUCTION_TERMINEE)
                                 .titre("Production terminée - " + ordre.getNumeroOrdre())
                                 .description("La production de " + ordre.getProduit().getNom() + " (" + ordre.getQuantite() + " unités) est terminée.")
                                 .priorite("MOYENNE")
                                 .resolue(false)
                                 .ordreProduction(ordre)
                                 .build();
                 sauvegarderEtNotifier(alerte);
         }

         @Override
         @Transactional
         public void creerAlerteProblemeSignale(String titre, String description, String priorite) {
                 Alerte alerte = Alerte.builder()
                                 .type(TypeAlerte.PROBLEME_SIGNALE)
                                 .titre(titre)
                                 .description(description)
                                 .priorite(priorite)
                                 .resolue(false)
                                 .build();
                 sauvegarderEtNotifier(alerte);
         }

         @Override
         @Transactional
         public void creerAlerteNouvelleCommande(Vente vente) {
                 Alerte alerte = Alerte.builder()
                                 .type(TypeAlerte.NOUVELLE_COMMANDE)
                                 .titre("Nouvelle commande - " + vente.getNumeroVente())
                                 .description("Une vente de " + vente.getMontantTotal() + " DH vient d'être réalisée.")
                                 .priorite("BASSE")
                                 .resolue(false)
                                 .vente(vente)
                                 .build();
                 sauvegarderEtNotifier(alerte);
         }

        @Override
        public List<AlerteResponse> getAlertesNonResolues() {
                return alerteRepository.findAlertesNonResoluesTriees().stream().map(this::mapToResponse).toList();
        }

        @Override
        public List<AlerteResponse> getAlertesParType(TypeAlerte type) {
                return alerteRepository.findByType(type).stream().map(this::mapToResponse).toList();
        }

        @Override
        public List<AlerteResponse> getAlertesParPriorite(String priorite) {
                return alerteRepository.findByPriorite(priorite).stream().map(this::mapToResponse).toList();
        }

        @Override
        public List<AlerteResponse> getAlertesParPeriode(LocalDateTime debut, LocalDateTime fin) {
                return alerteRepository.findByDateCreationBetween(debut, fin).stream().map(this::mapToResponse)
                                .toList();
        }

        @Override
        public Long countAlertesNonResolues() {
                return alerteRepository.countByResolueFalse();
        }

        @Override
        public Long countAlertesHautePriorite() {
                return alerteRepository.countByPrioriteAndResolueFalse("HAUTE");
        }

        @Override
        @Transactional
        public AlerteResponse resoudreAlerte(Long id, String commentaire) {
                Alerte alerte = alerteRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Alerte non trouvée"));
                alerte.setResolue(true);
                alerte.setDateResolution(LocalDateTime.now());
                if (commentaire != null)
                        alerte.setDescription(alerte.getDescription() + "\nRésolution: " + commentaire);
                return mapToResponse(alerteRepository.save(alerte));
        }

        @Override
        @Transactional
        public AlerteResponse ignorerAlerte(Long id, String raison) {
                Alerte alerte = alerteRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Alerte non trouvée"));
                alerte.setResolue(true);
                alerte.setDescription(alerte.getDescription() + "\nIgnorée: " + raison);
                return mapToResponse(alerteRepository.save(alerte));
        }

        @Override
        @Transactional
        public void supprimerAlerte(Long id) {
                alerteRepository.deleteById(id);
        }

        @Override
        @Transactional
        @Scheduled(cron = "0 0 6 * * ?")
        public void verifierAlertesAutomatiques() {
                log.info("Vérification automatique des alertes...");
                ingredientRepository.findIngredientsStockFaible().forEach(this::creerAlerteStockFaible);
                ingredientRepository.findIngredientsExpires().forEach(this::creerAlerteExpiration);
        }

        @Override
        public void verifierAlertesIngredient(Long ingredientId) {
                ingredientRepository.findById(ingredientId).ifPresent(i -> {
                        if (i.estEnRupture())
                                creerAlerteRuptureStock(i);
                        else if (i.estStockFaible())
                                creerAlerteStockFaible(i);
                });
        }

        @Override
        public void verifierCommandesEnRetard() {
                commandeAchatRepository.findCommandesEnRetard().forEach(this::creerAlerteCommandeRetard);
        }

        @Override
        public void verifierProductionsEnRetard() {
                ordreProductionRepository.findOrdresEnRetard().forEach(this::creerAlerteProductionRetard);
        }

        private AlerteResponse mapToResponse(Alerte alerte) {
                return AlerteResponse.builder()
                                .id(alerte.getId()).type(alerte.getType()).titre(alerte.getTitre())
                                .description(alerte.getDescription())
                                .dateCreation(alerte.getDateCreation()).dateResolution(alerte.getDateResolution())
                                .resolue(alerte.isResolue())
                                .priorite(alerte.getPriorite())
                                .ingredientId(alerte.getIngredient() != null ? alerte.getIngredient().getId() : null)
                                .produitId(alerte.getProduit() != null ? alerte.getProduit().getId() : null)
                                .commandeId(alerte.getCommande() != null ? alerte.getCommande().getId() : null)
                                .ordreProductionId(alerte.getOrdreProduction() != null
                                                ? alerte.getOrdreProduction().getId()
                                                : null)
                                .utilisateurId(alerte.getUtilisateur() != null ? alerte.getUtilisateur().getId() : null)
                                .build();
        }
}