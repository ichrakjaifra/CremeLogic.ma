package ma.cremelogic.CremeLogic.ma.service.impl;

import ma.cremelogic.CremeLogic.ma.dto.response.DashboardResponse;
import ma.cremelogic.CremeLogic.ma.entity.*;
import ma.cremelogic.CremeLogic.ma.repository.*;
import ma.cremelogic.CremeLogic.ma.service.interfaces.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final ProduitRepository produitRepository;
    private final IngredientRepository ingredientRepository;
    private final VenteRepository venteRepository;
    private final CommandeAchatRepository commandeAchatRepository;
    private final OrdreProductionRepository ordreProductionRepository;
    private final AlerteRepository alerteRepository;
    private final UtilisateurRepository utilisateurRepository;

    @Override
    public DashboardResponse getDashboardAdmin() {
        LocalDate aujourdhui = LocalDate.now();
        LocalDate debutMois = aujourdhui.withDayOfMonth(1);
        LocalDate debutAnnee = aujourdhui.withDayOfYear(1);

        LocalDateTime maintenant = LocalDateTime.now();
        LocalDateTime debutJour = maintenant.toLocalDate().atStartOfDay();
        LocalDateTime finJour = debutJour.plusDays(1);

        // Statistiques générales
        long totalProduits = produitRepository.count();
        long totalIngredients = ingredientRepository.count();

        // Ventes du jour
        List<Vente> ventesJour = venteRepository.findByDateVenteBetween(debutJour, finJour);
        long totalVentesJour = ventesJour.size();
        BigDecimal chiffreAffairesJour = ventesJour.stream()
                .map(Vente::getMontantTotal)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Commandes du jour
        List<CommandeAchat> commandesJour = commandeAchatRepository.findByDateCommandeBetween(aujourdhui, aujourdhui);
        long totalCommandesJour = commandesJour.size();

        // Productions du jour
        List<OrdreProduction> productionsJour = ordreProductionRepository.findByDateCreation(aujourdhui);
        long totalProductionsJour = productionsJour.size();

        // Chiffre d'affaires
        BigDecimal chiffreAffairesMois = getChiffreAffairesPeriode(debutMois.atStartOfDay(), maintenant);
        BigDecimal chiffreAffairesAnnee = getChiffreAffairesPeriode(debutAnnee.atStartOfDay(), maintenant);

        // Dépenses (simplifié)
        BigDecimal depensesJour = commandesJour.stream()
                .map(CommandeAchat::getMontantTotal)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal depensesMois = getDepensesMois(debutMois, aujourdhui);
        BigDecimal beneficeMois = chiffreAffairesMois.subtract(depensesMois);

        // Stocks
        long produitsStockFaible = produitRepository.findProduitsStockFaible().size();
        long ingredientsStockFaible = ingredientRepository.findIngredientsStockFaible().size();
        long ingredientsExpirant = ingredientRepository.findIngredientsExpirantAvant(aujourdhui.plusDays(7)).size();

        BigDecimal valeurStockIngredients = ingredientRepository.getValeurStockTotal();
        BigDecimal valeurStockProduits = produitRepository.findAll().stream()
                .map(p -> p.getPrixVente().multiply(new BigDecimal(p.getStockDisponible())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal valeurStockTotal = valeurStockIngredients.add(valeurStockProduits);

        // Alertes
        long alertesNonResolues = alerteRepository.findByResolue(false).size();
        long alertesHautePriorite = alerteRepository.countAlertesHautePrioriteNonResolues();

        // Graphiques
        Map<String, BigDecimal> ventesParMois = getVentesParMois(6);
        Map<String, BigDecimal> ventesParCategorie = getVentesParCategorie(debutMois, aujourdhui);
        Map<String, Long> produitsPlusVendus = getProduitsPlusVendus(debutMois, aujourdhui, 5);

        // Activités récentes
        List<VenteResponse> ventesRecent = venteRepository.findRecentVentes(5).stream()
                .map(this::mapVenteToResponse)
                .toList();

        List<CommandeAchatResponse> commandesRecent = commandeAchatRepository.findRecentCommandes(5).stream()
                .map(this::mapCommandeToResponse)
                .toList();

        List<OrdreProductionResponse> productionsRecent = ordreProductionRepository.findAll().stream()
                .sorted((o1, o2) -> o2.getDateCreation().compareTo(o1.getDateCreation()))
                .limit(5)
                .map(this::mapOrdreToResponse)
                .toList();

        List<AlerteResponse> alertesRecent = alerteRepository.findAlertesNonResoluesTriees().stream()
                .limit(5)
                .map(this::mapAlerteToResponse)
                .toList();

        return DashboardResponse.builder()
                .totalProduits(totalProduits)
                .totalIngredients(totalIngredients)
                .totalVentesJour(totalVentesJour)
                .totalCommandesJour(totalCommandesJour)
                .totalProductionsJour(totalProductionsJour)
                .chiffreAffairesJour(chiffreAffairesJour)
                .chiffreAffairesMois(chiffreAffairesMois)
                .chiffreAffairesAnnee(chiffreAffairesAnnee)
                .depensesJour(depensesJour)
                .depensesMois(depensesMois)
                .beneficeMois(beneficeMois)
                .produitsStockFaible(produitsStockFaible)
                .ingredientsStockFaible(ingredientsStockFaible)
                .ingredientsExpirant(ingredientsExpirant)
                .valeurStockTotal(valeurStockTotal)
                .alertesNonResolues(alertesNonResolues)
                .alertesHautePriorite(alertesHautePriorite)
                .ventesParMois(ventesParMois)
                .ventesParCategorie(ventesParCategorie)
                .produitsPlusVendus(produitsPlusVendus)
                .ventesRecent(ventesRecent)
                .commandesRecent(commandesRecent)
                .productionsRecent(productionsRecent)
                .alertesRecent(alertesRecent)
                .build();
    }

    @Override
    public DashboardResponse getDashboardChef() {
        // Récupérer l'utilisateur connecté
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Utilisateur chef = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        LocalDate aujourdhui = LocalDate.now();

        // Récupérer les ordres de production du chef
        List<OrdreProduction> ordresChef = ordreProductionRepository.findByResponsableId(chef.getId());

        // Statistiques spécifiques au chef
        long ordresEnCours = ordresChef.stream()
                .filter(o -> o.getStatut().toString().equals("EN_COURS"))
                .count();

        long ordresPlanifies = ordresChef.stream()
                .filter(o -> o.getStatut().toString().equals("PLANIFIEE"))
                .count();

        long ordresEnRetard = ordresChef.stream()
                .filter(OrdreProduction::estEnRetard)
                .count();

        // Ingrédients en stock faible (pour les recettes du chef)
        List<Ingredient> ingredientsStockFaible = ingredientRepository.findIngredientsStockFaible();
        long ingredientsCritiques = ingredientsStockFaible.size();

        // Alertes de production
        long alertesProduction = alerteRepository.findByType(TypeAlerte.PRODUCTION_RETARD).stream()
                .filter(a -> !a.isResolue())
                .count();

        return DashboardResponse.builder()
                .totalProductionsJour(ordresEnCours)
                .produitsStockFaible(0L) // Adapté pour le chef
                .ingredientsStockFaible(ingredientsCritiques)
                .alertesNonResolues(alertesProduction)
                .build();
    }

    @Override
    public DashboardResponse getDashboardMagasinier() {
        LocalDate aujourdhui = LocalDate.now();

        // Statistiques spécifiques au magasinier
        List<Ingredient> ingredientsStockFaible = ingredientRepository.findIngredientsStockFaible();
        List<Ingredient> ingredientsExpires = ingredientRepository.findIngredientsExpires();
        List<Ingredient> ingredientsEnRupture = ingredientRepository.findIngredientsEnRupture();

        // Commandes en attente de réception
        List<CommandeAchat> commandesEnAttente = commandeAchatRepository.findByStatut(StatutCommande.EN_ATTENTE);
        long commandesARéceptionner = commandesEnAttente.size();

        // Valeur du stock
        BigDecimal valeurStockIngredients = ingredientRepository.getValeurStockTotal();
        BigDecimal valeurStockProduits = produitRepository.findAll().stream()
                .map(p -> p.getPrixVente().multiply(new BigDecimal(p.getStockDisponible())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal valeurStockTotal = valeurStockIngredients.add(valeurStockProduits);

        // Alertes de stock
        long alertesStock = alerteRepository.findByType(TypeAlerte.STOCK_FAIBLE).stream()
                .filter(a -> !a.isResolue())
                .count();

        long alertesExpiration = alerteRepository.findByType(TypeAlerte.DATE_EXPIRATION).stream()
                .filter(a -> !a.isResolue())
                .count();

        return DashboardResponse.builder()
                .ingredientsStockFaible((long) ingredientsStockFaible.size())
                .ingredientsExpirant((long) ingredientsExpires.size())
                .valeurStockTotal(valeurStockTotal)
                .alertesNonResolues(alertesStock + alertesExpiration)
                .build();
    }

    @Override
    public DashboardResponse getDashboardEmploye() {
        // Récupérer l'utilisateur connecté
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Utilisateur employe = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        LocalDate aujourdhui = LocalDate.now();

        // Récupérer les ordres de production assignés à l'employé
        List<OrdreProduction> ordresEmploye = ordreProductionRepository.findByResponsableId(employe.getId());

        // Statistiques spécifiques à l'employé
        long tachesEnCours = ordresEmploye.stream()
                .filter(o -> o.getStatut().toString().equals("EN_COURS"))
                .count();

        long tachesTermineesAujourdhui = ordresEmploye.stream()
                .filter(o -> o.getStatut().toString().equals("TERMINEE") &&
                        o.getDateFinReelle() != null &&
                        o.getDateFinReelle().isEqual(aujourdhui))
                .count();

        long tachesEnRetard = ordresEmploye.stream()
                .filter(OrdreProduction::estEnRetard)
                .count();

        return DashboardResponse.builder()
                .totalProductionsJour(tachesEnCours)
                .build();
    }

    // Méthodes auxiliaires
    private BigDecimal getChiffreAffairesPeriode(LocalDateTime debut, LocalDateTime fin) {
        Double ca = venteRepository.getChiffreAffairesPeriode(debut, fin);
        return ca != null ? BigDecimal.valueOf(ca) : BigDecimal.ZERO;
    }

    private BigDecimal getDepensesMois(LocalDate debut, LocalDate fin) {
        Double depenses = commandeAchatRepository.getMontantTotalCommandesPeriode(debut, fin);
        return depenses != null ? BigDecimal.valueOf(depenses) : BigDecimal.ZERO;
    }

    private Map<String, BigDecimal> getVentesParMois(int nbMois) {
        Map<String, BigDecimal> result = new LinkedHashMap<>();

        for (int i = nbMois - 1; i >= 0; i--) {
            YearMonth mois = YearMonth.now().minusMonths(i);
            LocalDate debut = mois.atDay(1);
            LocalDate fin = mois.atEndOfMonth();

            LocalDateTime debutDateTime = debut.atStartOfDay();
            LocalDateTime finDateTime = fin.atTime(23, 59, 59);

            BigDecimal caMois = getChiffreAffairesPeriode(debutDateTime, finDateTime);
            result.put(mois.getMonth().toString().substring(0, 3) + " " + mois.getYear(), caMois);
        }

        return result;
    }

    private Map<String, BigDecimal> getVentesParCategorie(LocalDate debut, LocalDate fin) {
        // Implémentation simplifiée
        Map<String, BigDecimal> result = new HashMap<>();

        // Pour chaque catégorie de produit, calculer le CA
        Arrays.stream(CategorieProduit.values()).forEach(categorie -> {
            List<Produit> produitsCategorie = produitRepository.findByCategorie(categorie);
            BigDecimal caCategorie = produitsCategorie.stream()
                    .map(produit -> {
                        // Calculer les ventes pour ce produit dans la période
                        // (à implémenter avec une requête plus complexe)
                        return BigDecimal.ZERO;
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            result.put(categorie.toString(), caCategorie);
        });

        return result;
    }

    private Map<String, Long> getProduitsPlusVendus(LocalDate debut, LocalDate fin, int limit) {
        // Implémentation simplifiée
        Map<String, Long> result = new LinkedHashMap<>();

        // Récupérer les produits et calculer les quantités vendues
        // (à implémenter avec une requête plus complexe)

        return result;
    }

    // Méthodes de mapping
    private VenteResponse mapVenteToResponse(Vente vente) {
        return VenteResponse.builder()
                .id(vente.getId())
                .numeroVente(vente.getNumeroVente())
                .dateVente(vente.getDateVente())
                .montantTotal(vente.getMontantTotal())
                .nomClient(vente.getNomClient())
                .build();
    }

    private CommandeAchatResponse mapCommandeToResponse(CommandeAchat commande) {
        return CommandeAchatResponse.builder()
                .id(commande.getId())
                .numeroCommande(commande.getNumeroCommande())
                .dateCommande(commande.getDateCommande())
                .montantTotal(commande.getMontantTotal())
                .fournisseurNom(commande.getFournisseur().getNom())
                .build();
    }

    private OrdreProductionResponse mapOrdreToResponse(OrdreProduction ordre) {
        return OrdreProductionResponse.builder()
                .id(ordre.getId())
                .numeroOrdre(ordre.getNumeroOrdre())
                .produitNom(ordre.getProduit().getNom())
                .quantite(ordre.getQuantite())
                .statut(ordre.getStatut())
                .build();
    }

    private AlerteResponse mapAlerteToResponse(Alerte alerte) {
        return AlerteResponse.builder()
                .id(alerte.getId())
                .titre(alerte.getTitre())
                .description(alerte.getDescription())
                .priorite(alerte.getPriorite())
                .dateCreation(alerte.getDateCreation())
                .build();
    }
}
