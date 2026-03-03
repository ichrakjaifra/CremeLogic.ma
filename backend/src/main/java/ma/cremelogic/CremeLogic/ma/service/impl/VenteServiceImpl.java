package ma.cremelogic.CremeLogic.ma.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.cremelogic.CremeLogic.ma.dto.request.LigneVenteRequest;
import ma.cremelogic.CremeLogic.ma.dto.request.VenteRequest;
import ma.cremelogic.CremeLogic.ma.dto.response.LigneVenteResponse;
import ma.cremelogic.CremeLogic.ma.dto.response.ProduitResponse;
import ma.cremelogic.CremeLogic.ma.dto.response.VenteResponse;
import ma.cremelogic.CremeLogic.ma.entity.*;
import ma.cremelogic.CremeLogic.ma.enums.ModePaiement;
import ma.cremelogic.CremeLogic.ma.exception.ResourceNotFoundException;
import ma.cremelogic.CremeLogic.ma.exception.ValidationException;
import ma.cremelogic.CremeLogic.ma.repository.*;
import ma.cremelogic.CremeLogic.ma.service.interfaces.AlerteService;
import ma.cremelogic.CremeLogic.ma.service.interfaces.MouvementStockService;
import ma.cremelogic.CremeLogic.ma.service.interfaces.VenteService;
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
public class VenteServiceImpl implements VenteService {

    private final VenteRepository venteRepository;
    private final ProduitRepository produitRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final MouvementStockService mouvementStockService;
    private final AlerteService alerteService;
    private final HistoriqueService historiqueService;

    @Override
    @Transactional
    public VenteResponse createVente(VenteRequest request) {
        Utilisateur caissier = getCurrentUser();

        Vente vente = Vente.builder()
                .modePaiement(request.getModePaiement())
                .montantPaye(request.getMontantPaye())
                .nomClient(request.getNomClient())
                .telephoneClient(request.getTelephoneClient())
                .emailClient(request.getEmailClient())
                .notes(request.getNotes())
                .caissier(caissier)
                .build();

        // Ajouter les lignes de vente
        for (LigneVenteRequest ligneRequest : request.getLignesVente()) {
            Produit produit = produitRepository.findById(ligneRequest.getProduitId())
                    .orElseThrow(() -> new ResourceNotFoundException("Produit", "id", ligneRequest.getProduitId()));

            // Vérifier le stock
            if (produit.getStockDisponible() < ligneRequest.getQuantite()) {
                throw new ValidationException("Stock insuffisant pour le produit: " + produit.getNom() +
                        " (Disponible: " + produit.getStockDisponible() + ")");
            }

            LigneVente ligne = LigneVente.builder()
                    .vente(vente)
                    .produit(produit)
                    .quantite(ligneRequest.getQuantite())
                    .prixUnitaire(produit.getPrixVente())
                    .remise(ligneRequest.getRemise())
                    .build();

            vente.getLignesVente().add(ligne);

            // Mettre à jour le stock
            produit.setStockDisponible(produit.getStockDisponible() - ligneRequest.getQuantite());
            produitRepository.save(produit);
        }

        vente.calculerMontantTotal();
        vente.setMontantRendu(vente.getMontantPaye().subtract(vente.getMontantTotal()));

        Vente saved = venteRepository.save(vente);

        // Créer des mouvements de stock pour les sorties
        for (LigneVente ligne : saved.getLignesVente()) {
            // Cette partie serait implémentée dans MouvementStockService
            log.info("Déduction stock pour produit {}: -{}", ligne.getProduit().getNom(), ligne.getQuantite());
        }

        historiqueService.enregistrerCreation("VENTE", saved.getId(),
                "Vente enregistrée: " + saved.getNumeroVente() +
                        " - Montant: " + saved.getMontantTotal());

        log.info("Vente enregistrée: {} - Montant: {}", saved.getNumeroVente(), saved.getMontantTotal());
        return mapToResponse(saved);
    }

    @Override
    public VenteResponse getVente(Long id) {
        Vente vente = venteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vente", "id", id));
        return mapToResponse(vente);
    }

    @Override
    public List<VenteResponse> getAllVentes() {
        return venteRepository.findAllByOrderByDateVenteDesc().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<VenteResponse> getVentesByDate(LocalDateTime debut, LocalDateTime fin) {
        return venteRepository.findByDateVenteBetweenOrderByDateVenteDesc(debut, fin).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<VenteResponse> getVentesByCaissier(Long caissierId) {
        return venteRepository.findByCaissierIdOrderByDateVenteDesc(caissierId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<VenteResponse> searchVentesByClient(String recherche) {
        return venteRepository.findByNomClientContainingIgnoreCaseOrTelephoneClientContainingIgnoreCase(recherche, recherche).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void annulerVente(Long id, String raison) {
        Vente vente = venteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vente", "id", id));

        if (vente.getDateVente().isBefore(LocalDateTime.now().minusDays(1))) {
            throw new ValidationException("Impossible d'annuler une vente de plus d'un jour");
        }

        // Remettre les produits en stock
        for (LigneVente ligne : vente.getLignesVente()) {
            Produit produit = ligne.getProduit();
            produit.setStockDisponible(produit.getStockDisponible() + ligne.getQuantite());
            produitRepository.save(produit);
        }

        // Marquer la vente comme annulée (on pourrait ajouter un statut)
        vente.setNotes((vente.getNotes() != null ? vente.getNotes() : "") +
                "\nANNULÉE: " + raison);

        venteRepository.save(vente);

        // Créer une alerte
        alerteService.creerAlerteVenteAnnulee(vente, raison);

        historiqueService.enregistrerSuppression("VENTE", id,
                "Annulation de la vente: " + vente.getNumeroVente() + " - Raison: " + raison);

        log.info("Vente annulée: {} - Raison: {}", vente.getNumeroVente(), raison);
    }

    @Override
    public VenteResponse genererFacture(Long id) {
        Vente vente = venteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vente", "id", id));

        // Ici on générerait un PDF ou autre format de facture
        log.info("Facture générée pour la vente: {}", vente.getNumeroVente());

        return mapToResponse(vente);
    }

    @Override
    public BigDecimal getChiffreAffairesPeriode(LocalDateTime debut, LocalDateTime fin) {
        Double ca = venteRepository.getChiffreAffairesPeriode(debut, fin);
        return ca != null ? BigDecimal.valueOf(ca) : BigDecimal.ZERO;
    }

    @Override
    public Long getNombreVentesPeriode(LocalDateTime debut, LocalDateTime fin) {
        return venteRepository.getNombreVentesPeriode(debut, fin);
    }

    @Override
    public List<VenteResponse> getVentesRecent(int limit) {
        return venteRepository.findRecentVentes(limit).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, BigDecimal> getVentesParCategorie(LocalDateTime debut, LocalDateTime fin) {
        List<Vente> ventes = venteRepository.findByDateVenteBetween(debut, fin);
        Map<String, BigDecimal> result = new HashMap<>();

        for (Vente vente : ventes) {
            for (LigneVente ligne : vente.getLignesVente()) {
                String categorie = ligne.getProduit().getCategorie().toString();
                result.merge(categorie, ligne.getMontantTotal(), BigDecimal::add);
            }
        }

        return result;
    }

    @Override
    public List<ProduitResponse> getProduitsPlusVendus(LocalDateTime debut, LocalDateTime fin, int limit) {
        List<Vente> ventes = venteRepository.findByDateVenteBetween(debut, fin);
        Map<Long, Long> quantitesVendues = new HashMap<>();

        for (Vente vente : ventes) {
            for (LigneVente ligne : vente.getLignesVente()) {
                quantitesVendues.merge(ligne.getProduit().getId(),
                        ligne.getQuantite().longValue(), Long::sum);
            }
        }

        return quantitesVendues.entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .limit(limit)
                .map(entry -> {
                    Produit produit = produitRepository.findById(entry.getKey()).orElse(null);
                    return produit != null ? mapProduitToResponse(produit) : null;
                })
                .filter(p -> p != null)
                .collect(Collectors.toList());
    }

    private Utilisateur getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "email", email));
    }

    private VenteResponse mapToResponse(Vente vente) {
        List<LigneVenteResponse> lignes = vente.getLignesVente().stream()
                .map(l -> LigneVenteResponse.builder()
                        .id(l.getId())
                        .venteId(vente.getId())
                        .produitId(l.getProduit().getId())
                        .produitNom(l.getProduit().getNom())
                        .produitCode(l.getProduit().getCodeProduit())
                        .quantite(l.getQuantite())
                        .prixUnitaire(l.getPrixUnitaire())
                        .remise(l.getRemise())
                        .montantTotal(l.getMontantTotal())
                        .build())
                .collect(Collectors.toList());

        return VenteResponse.builder()
                .id(vente.getId())
                .numeroVente(vente.getNumeroVente())
                .dateVente(vente.getDateVente())
                .modePaiement(vente.getModePaiement())
                .montantTotal(vente.getMontantTotal())
                .montantPaye(vente.getMontantPaye())
                .montantRendu(vente.getMontantRendu())
                .montantDu(vente.getMontantDu())
                .estPayee(vente.estPayee())
                .nomClient(vente.getNomClient())
                .telephoneClient(vente.getTelephoneClient())
                .emailClient(vente.getEmailClient())
                .notes(vente.getNotes())
                .caissierId(vente.getCaissier() != null ? vente.getCaissier().getId() : null)
                .caissierNom(vente.getCaissier() != null ?
                        vente.getCaissier().getNom() + " " + vente.getCaissier().getPrenom() : null)
                .dateCreation(vente.getDateCreation())
                .lignesVente(lignes)
                .build();
    }

    private ProduitResponse mapProduitToResponse(Produit produit) {
        return ProduitResponse.builder()
                .id(produit.getId())
                .codeProduit(produit.getCodeProduit())
                .nom(produit.getNom())
                .categorie(produit.getCategorie())
                .prixVente(produit.getPrixVente())
                .stockDisponible(produit.getStockDisponible())
                .build();
    }
}