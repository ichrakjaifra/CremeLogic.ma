package ma.cremelogic.CremeLogic.ma.service.interfaces;

import ma.cremelogic.CremeLogic.ma.dto.response.AlerteResponse;
import ma.cremelogic.CremeLogic.ma.entity.CommandeAchat;
import ma.cremelogic.CremeLogic.ma.entity.Ingredient;
import ma.cremelogic.CremeLogic.ma.entity.OrdreProduction;
import ma.cremelogic.CremeLogic.ma.entity.Vente;
import ma.cremelogic.CremeLogic.ma.enums.TypeAlerte;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface AlerteService {
    // Création d'alertes
    void creerAlerteStockFaible(Ingredient ingredient);
    void creerAlerteRuptureStock(Ingredient ingredient);
    void creerAlerteExpiration(Ingredient ingredient);
    void creerAlerteCommandeRetard(CommandeAchat commande);
    void creerAlerteCommandeAnnulee(CommandeAchat commande, String raison);
    void creerAlerteProductionRetard(OrdreProduction ordre);
    void creerAlerteProductionAnnulee(OrdreProduction ordre, String raison);
    void creerAlerteVenteAnnulee(Vente vente, String raison);
    void creerAlertePerteStock(Ingredient ingredient, BigDecimal quantite, String raison);
    void creerAlerteIngredientsInsuffisants(OrdreProduction ordre, List<String> ingredientsManquants);
    void creerAlerteProductionTerminee(OrdreProduction ordre);
    void creerAlerteProblemeSignale(String titre, String description, String priorite);
    void creerAlerteNouvelleCommande(Vente vente);
    void creerAlertePersonnalisee(String titre, String description, String priorite, TypeAlerte type);

    // Consultation des alertes
    List<AlerteResponse> getAlertesNonResolues();
    List<AlerteResponse> getAlertesParType(TypeAlerte type);
    List<AlerteResponse> getAlertesParPriorite(String priorite);
    List<AlerteResponse> getAlertesParPeriode(LocalDateTime debut, LocalDateTime fin);
    Long countAlertesNonResolues();
    Long countAlertesHautePriorite();

    // Gestion des alertes
    AlerteResponse resoudreAlerte(Long id, String commentaire);
    AlerteResponse ignorerAlerte(Long id, String raison);
    void supprimerAlerte(Long id);

    // Vérification automatique
    void verifierAlertesAutomatiques();
    void verifierAlertesIngredient(Long ingredientId);
    void verifierCommandesEnRetard();
    void verifierProductionsEnRetard();
}