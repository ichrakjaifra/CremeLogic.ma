package ma.cremelogic.CremeLogic.ma.service.impl;

import ma.cremelogic.CremeLogic.ma.dto.response.*;
import ma.cremelogic.CremeLogic.ma.entity.*;
import ma.cremelogic.CremeLogic.ma.enums.CategorieProduit;
import ma.cremelogic.CremeLogic.ma.enums.StatutCommande;
import ma.cremelogic.CremeLogic.ma.enums.TypeAlerte;
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

@Service
@RequiredArgsConstructor
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class DashboardServiceImpl implements DashboardService {

        private final ProduitRepository produitRepository;
        private final IngredientRepository ingredientRepository;
        private final VenteRepository venteRepository;
        private final CommandeAchatRepository commandeAchatRepository;
        private final OrdreProductionRepository ordreProductionRepository;
        private final AlerteRepository alerteRepository;
        private final UtilisateurRepository utilisateurRepository;
        private final RecetteRepository recetteRepository;
        private final TacheRepository tacheRepository;

        @Override
        public DashboardResponse getDashboardAdmin() {
                LocalDate aujourdhui = LocalDate.now();
                LocalDate debutMois = aujourdhui.withDayOfMonth(1);
                LocalDateTime maintenant = LocalDateTime.now();

                DashboardResponse.DashboardResponseBuilder builder = DashboardResponse.builder();
                builder.totalProduits(produitRepository.count());
                builder.totalIngredients(ingredientRepository.count());

                List<Vente> ventesJour = venteRepository.findByDateVenteBetween(aujourdhui.atStartOfDay(), maintenant);
                builder.totalVentesJour((long) ventesJour.size());
                builder.chiffreAffairesJour(ventesJour.stream().map(Vente::getMontantTotal).filter(Objects::nonNull)
                                .reduce(BigDecimal.ZERO, BigDecimal::add));

                builder.chiffreAffairesMois(getChiffreAffairesPeriode(debutMois.atStartOfDay(), maintenant));
                builder.depensesMois(getDepensesMois(debutMois, aujourdhui));
                builder.produitsStockFaible((long) produitRepository.findProduitsStockFaible().size());
                builder.ingredientsStockFaible((long) ingredientRepository.findIngredientsStockFaible().size());
                builder.alertesNonResolues(alerteRepository.countByResolueFalse());

                // Graphiques
                builder.ventesParMois(getVentesParMois(6));
                builder.beneficesParMois(getBeneficesParMois(6));
                builder.ventesParCategorie(getVentesParCategorie(debutMois, aujourdhui));

                // Données récentes (Top 5)
                builder.ventesRecent(venteRepository.findAll().stream()
                                .sorted((v1, v2) -> v2.getDateVente().compareTo(v1.getDateVente()))
                                .limit(5).map(this::mapVenteToResponse).toList());

                builder.productionsRecent(ordreProductionRepository.findAll().stream()
                                .sorted((p1, p2) -> p2.getDateCreation().compareTo(p1.getDateCreation()))
                                .limit(5).map(this::mapProductionToResponse).toList());

                builder.alertesRecent(alerteRepository.findByResolue(false).stream()
                                .sorted((a1, a2) -> a2.getDateCreation().compareTo(a1.getDateCreation()))
                                .limit(5).map(this::mapAlerteToResponse).toList());

                // Fournisseurs (Mocking evaluation if missing)
                List<Map<String, Object>> fournisseurs = new ArrayList<>();
                produitRepository.findAll().stream().limit(5).forEach(p -> {
                    Map<String, Object> f = new HashMap<>();
                    f.put("nom", "Fournisseur " + p.getNom());
                    f.put("evaluation", 4.5);
                    fournisseurs.add(f);
                });
                builder.topFournisseurs(fournisseurs);

                return builder.build();
        }

        private Map<String, BigDecimal> getBeneficesParMois(int nbMois) {
            Map<String, BigDecimal> result = new LinkedHashMap<>();
            Map<String, BigDecimal> ventes = getVentesParMois(nbMois);
            Map<String, BigDecimal> couts = getCoutsParMois(nbMois);
            
            ventes.forEach((mois, ca) -> {
                BigDecimal cout = couts.getOrDefault(mois, BigDecimal.ZERO);
                result.put(mois, ca.subtract(cout));
            });
            return result;
        }

        private OrdreProductionResponse mapProductionToResponse(OrdreProduction p) {
            return OrdreProductionResponse.builder()
                .id(p.getId())
                .numeroOrdre(p.getNumeroOrdre())
                .produitNom(p.getProduit().getNom())
                .quantite(p.getQuantite())
                .statut(p.getStatut())
                .dateCreation(p.getDateCreation())
                .build();
        }

        private AlerteResponse mapAlerteToResponse(Alerte a) {
            return AlerteResponse.builder()
                .id(a.getId())
                .titre(a.getTitre())
                .type(a.getType())
                .priorite(a.getPriorite())
                .dateCreation(a.getDateCreation())
                .resolue(a.isResolue())
                .build();
        }

        @Override
        public DashboardResponse getDashboardChef() {
                LocalDateTime debutJour = LocalDate.now().atStartOfDay();
                LocalDateTime finJour = LocalDate.now().atTime(23, 59, 59);

                DashboardResponse.DashboardResponseBuilder builder = DashboardResponse.builder();

                // Stats de production
                long productionsEnCours = ordreProductionRepository.findByStatutOrderByDateCreationDesc(
                                ma.cremelogic.CremeLogic.ma.enums.StatutProduction.EN_COURS).size();
                long productionsTerminees = ordreProductionRepository.findAll().stream()
                                .filter(p -> p.getStatut() == ma.cremelogic.CremeLogic.ma.enums.StatutProduction.TERMINEE
                                                && ( (p.getDateFinReelle() != null && p.getDateFinReelle().equals(LocalDate.now())) 
                                                    || (p.getDateModification() != null && p.getDateModification().isAfter(debutJour)) ))
                                .count();

                builder.totalProductionsJour(productionsEnCours);
                builder.totalProductionsTermineesJour(productionsTerminees);
                builder.totalRecettesActives(recetteRepository.count());

                // Ordres récents
                builder.productionsRecent(ordreProductionRepository.findAll().stream()
                                .sorted((p1, p2) -> p2.getDateCreation().compareTo(p1.getDateCreation()))
                                .limit(10).map(this::mapProductionToResponse).toList());

                // Ingrédients stock faible
                List<Ingredient> ingredientsFaibles = ingredientRepository.findIngredientsStockFaible();
                builder.ingredientsStockFaible((long) ingredientsFaibles.size());
                builder.ingredientsCritiques(ingredientsFaibles.stream()
                                .limit(5).map(this::mapIngredientToResponse).toList());

                // Recettes populaires (Simulé par les dernières recettes utilisées)
                builder.recettesPopulaires(recetteRepository.findAll().stream()
                                .limit(5).map(this::mapRecetteToResponse).toList());

                // Tâches du jour
                String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
                Optional<Utilisateur> currentUser = utilisateurRepository.findByEmail(currentUserEmail);

                List<Tache> taches = tacheRepository.findAll().stream()
                                .filter(t -> t.getDateEcheance() == null || (t.getDateEcheance().isAfter(debutJour)
                                                && t.getDateEcheance().isBefore(finJour)))
                                .filter(t -> t.getAssigneA() == null || (currentUser.isPresent()
                                                && t.getAssigneA().getId().equals(currentUser.get().getId())))
                                .sorted((t1, t2) -> t1.getPriorite().compareTo(t2.getPriorite()))
                                .limit(5).toList();

                builder.tachesDuJour(taches.stream().map(this::mapTacheToResponse).toList());

                return builder.build();
        }

        private IngredientResponse mapIngredientToResponse(Ingredient i) {
                return IngredientResponse.builder()
                                .id(i.getId())
                                .nom(i.getNom())
                                .quantiteStock(i.getQuantiteStock())
                                .quantiteMinimum(i.getQuantiteMinimum())
                                .uniteMesure(i.getUniteMesure())
                                .build();
        }

        private RecetteResponse mapRecetteToResponse(Recette r) {
                return RecetteResponse.builder()
                                .id(r.getId())
                                .nom(r.getNom())
                                .coutTotal(r.getCoutTotal())
                                .nombrePortions(r.getNombrePortions())
                                .build();
        }

        private TacheResponse mapTacheToResponse(Tache t) {
                return TacheResponse.builder()
                                .id(t.getId())
                                .titre(t.getTitre())
                                .description(t.getDescription())
                                .statut(t.getStatut())
                                .priorite(t.getPriorite())
                                .dateEcheance(t.getDateEcheance())
                                .assigneANom(t.getAssigneA() != null ? t.getAssigneA().getNom() : "Non assigné")
                                .dateCreation(t.getDateCreation())
                                .build();
        }

        @Override
        public DashboardResponse getDashboardMagasinier() {
                DashboardResponse.DashboardResponseBuilder builder = DashboardResponse.builder();
                builder.ingredientsStockFaible((long) ingredientRepository.findIngredientsStockFaible().size());
                builder.totalCommandesJour(
                                (long) commandeAchatRepository.findByStatut(StatutCommande.EN_ATTENTE).size());
                return builder.build();
        }

        @Override
        public DashboardResponse getDashboardEmploye() {
                return DashboardResponse.builder().build();
        }

        @Override
        public Map<String, BigDecimal> getVentesParMois(int nbMois) {
                Map<String, BigDecimal> result = new LinkedHashMap<>();
                for (int i = nbMois - 1; i >= 0; i--) {
                        YearMonth mois = YearMonth.now().minusMonths(i);
                        BigDecimal ca = getChiffreAffairesPeriode(mois.atDay(1).atStartOfDay(),
                                        mois.atEndOfMonth().atTime(23, 59, 59));
                        result.put(mois.toString(), ca);
                }
                return result;
        }

        @Override
        public Map<String, BigDecimal> getVentesParCategorie(LocalDate debut, LocalDate fin) {
                Map<String, BigDecimal> result = new HashMap<>();
                for (CategorieProduit cat : CategorieProduit.values()) {
                        result.put(cat.name(), BigDecimal.ZERO); // Simplified
                }
                return result;
        }

        @Override
        public Map<String, Long> getProduitsPlusVendus(LocalDate debut, LocalDate fin, int limit) {
                return new HashMap<>();
        }

        @Override
        public Map<String, BigDecimal> getCoutsParMois(int nbMois) {
                Map<String, BigDecimal> result = new LinkedHashMap<>();
                for (int i = nbMois - 1; i >= 0; i--) {
                        YearMonth mois = YearMonth.now().minusMonths(i);
                        result.put(mois.toString(), getDepensesMois(mois.atDay(1), mois.atEndOfMonth()));
                }
                return result;
        }

        @Override
        public Map<String, Long> getAlertesParType(LocalDate debut, LocalDate fin) {
                return new HashMap<>();
        }

        @Override
        public Map<String, Object> getPrevisionsStock() {
                return new HashMap<>();
        }

        @Override
        public Map<String, Object> getPrevisionsVentes() {
                return new HashMap<>();
        }

        @Override
        public List<Map<String, Object>> getSuggestionsAchats() {
                return new ArrayList<>();
        }

        private BigDecimal getChiffreAffairesPeriode(LocalDateTime debut, LocalDateTime fin) {
                Double ca = venteRepository.getChiffreAffairesPeriode(debut, fin);
                return ca != null ? BigDecimal.valueOf(ca) : BigDecimal.ZERO;
        }

        private BigDecimal getDepensesMois(LocalDate debut, LocalDate fin) {
                Double depenses = commandeAchatRepository.getMontantTotalCommandesPeriode(debut, fin);
                return depenses != null ? BigDecimal.valueOf(depenses) : BigDecimal.ZERO;
        }

        private VenteResponse mapVenteToResponse(Vente v) {
                return VenteResponse.builder().id(v.getId()).numeroVente(v.getNumeroVente()).build();
        }
}
