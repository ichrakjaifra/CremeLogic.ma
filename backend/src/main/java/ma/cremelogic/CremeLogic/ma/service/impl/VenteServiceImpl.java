package ma.cremelogic.CremeLogic.ma.service.impl;

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
import ma.cremelogic.CremeLogic.ma.repository.VenteRepository;
import ma.cremelogic.CremeLogic.ma.repository.ProduitRepository;
import ma.cremelogic.CremeLogic.ma.repository.UtilisateurRepository;
import ma.cremelogic.CremeLogic.ma.service.interfaces.AlerteService;
import ma.cremelogic.CremeLogic.ma.service.interfaces.MouvementStockService;
import ma.cremelogic.CremeLogic.ma.service.interfaces.VenteService;
import org.springframework.beans.factory.annotation.Autowired;
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
@Slf4j
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class VenteServiceImpl implements VenteService {

    private final VenteRepository venteRepository;
    private final ProduitRepository produitRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final MouvementStockService mouvementStockService;
    private final AlerteService alerteService;
    private final HistoriqueService historiqueService;

    @Autowired
    public VenteServiceImpl(VenteRepository venteRepository,
            ProduitRepository produitRepository,
            UtilisateurRepository utilisateurRepository,
            MouvementStockService mouvementStockService,
            AlerteService alerteService,
            HistoriqueService historiqueService) {
        this.venteRepository = venteRepository;
        this.produitRepository = produitRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.mouvementStockService = mouvementStockService;
        this.alerteService = alerteService;
        this.historiqueService = historiqueService;
    }

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

        for (LigneVenteRequest ligneRequest : request.getLignesVente()) {
            Produit produit = produitRepository.findById(ligneRequest.getProduitId())
                    .orElseThrow(() -> new ResourceNotFoundException("Produit", "id", ligneRequest.getProduitId()));

            if (produit.getStockDisponible() < ligneRequest.getQuantite()) {
                throw new ValidationException("Stock insuffisant for product: " + produit.getNom());
            }

            BigDecimal quantiteBD = new BigDecimal(ligneRequest.getQuantite());
            BigDecimal totalLigne = produit.getPrixVente().multiply(quantiteBD);
            BigDecimal remiseLigne = ligneRequest.getRemise();
            if (remiseLigne != null && remiseLigne.compareTo(BigDecimal.ZERO) > 0) {
                totalLigne = totalLigne.subtract(remiseLigne);
            }

            LigneVente ligne = LigneVente.builder()
                    .vente(vente)
                    .produit(produit)
                    .quantite(ligneRequest.getQuantite())
                    .prixUnitaire(produit.getPrixVente())
                    .remise(remiseLigne)
                    .montantTotal(totalLigne)
                    .build();

            vente.getLignesVente().add(ligne);
            produit.setStockDisponible(produit.getStockDisponible() - ligneRequest.getQuantite());
            produitRepository.save(produit);
        }

        vente.calculerMontantTotal();
        vente.setMontantRendu(vente.getMontantPaye().subtract(vente.getMontantTotal()));

        Vente saved = venteRepository.save(vente);
        alerteService.creerAlerteNouvelleCommande(saved);

        historiqueService.enregistrerCreation("VENTE", saved.getId(), "Vente " + saved.getNumeroVente());
        log.info("Vente enregistrée: {}", saved.getNumeroVente());
        return mapToResponse(saved);
    }

    @Override
    public VenteResponse getVente(Long id) {
        return mapToResponse(
                venteRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Vente", "id", id)));
    }

    @Override
    public List<VenteResponse> getAllVentes() {
        return venteRepository.findAllByOrderByDateVenteDesc().stream().map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<VenteResponse> getVentesByDate(LocalDateTime debut, LocalDateTime fin) {
        return venteRepository.findByDateVenteBetweenOrderByDateVenteDesc(debut, fin).stream().map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<VenteResponse> getVentesByCaissier(Long caissierId) {
        return venteRepository.findByCaissierIdOrderByDateVenteDesc(caissierId).stream().map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<VenteResponse> searchVentesByClient(String recherche) {
        return venteRepository
                .findByNomClientContainingIgnoreCaseOrTelephoneClientContainingIgnoreCase(recherche, recherche).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void annulerVente(Long id, String raison) {
        Vente vente = venteRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Vente", "id", id));
        if (vente.getDateVente().isBefore(LocalDateTime.now().minusDays(1)))
            throw new ValidationException("Délai d'annulation dépassé");

        for (LigneVente ligne : vente.getLignesVente()) {
            Produit p = ligne.getProduit();
            p.setStockDisponible(p.getStockDisponible() + ligne.getQuantite());
            produitRepository.save(p);
        }

        vente.setNotes((vente.getNotes() != null ? vente.getNotes() : "") + "\nANNULÉE: " + raison);
        venteRepository.save(vente);
        alerteService.creerAlerteVenteAnnulee(vente, raison);
        historiqueService.enregistrerSuppression("VENTE", id, "Annulation vente " + vente.getNumeroVente());
    }

    @Override
    public VenteResponse genererFacture(Long id) {
        return mapToResponse(
                venteRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Vente", "id", id)));
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
        return venteRepository.findRecentVentes(limit).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public Map<String, BigDecimal> getVentesParCategorie(LocalDateTime debut, LocalDateTime fin) {
        Map<String, BigDecimal> result = new HashMap<>();
        venteRepository.findByDateVenteBetween(debut, fin).forEach(v -> v.getLignesVente().forEach(l -> {
            result.merge(l.getProduit().getCategorie().toString(), l.getMontantTotal(), BigDecimal::add);
        }));
        return result;
    }

    @Override
    public List<ProduitResponse> getProduitsPlusVendus(LocalDateTime debut, LocalDateTime fin, int limit) {
        Map<Long, Long> counts = new HashMap<>();
        venteRepository.findByDateVenteBetween(debut, fin).forEach(v -> v.getLignesVente().forEach(l -> {
            counts.merge(l.getProduit().getId(), l.getQuantite().longValue(), Long::sum);
        }));
        return counts.entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .limit(limit)
                .map(e -> produitRepository.findById(e.getKey()).map(this::mapProduitToResponse).orElse(null))
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
    }

    private Utilisateur getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "email", email));
    }

    private VenteResponse mapToResponse(Vente vente) {
        List<LigneVenteResponse> lignes = vente.getLignesVente().stream()
                .map(l -> LigneVenteResponse.builder().id(l.getId()).produitId(l.getProduit().getId())
                        .produitNom(l.getProduit().getNom()).quantite(l.getQuantite()).prixUnitaire(l.getPrixUnitaire())
                        .remise(l.getRemise()).montantTotal(l.getMontantTotal()).build())
                .collect(Collectors.toList());
        return VenteResponse.builder()
                .id(vente.getId()).numeroVente(vente.getNumeroVente()).dateVente(vente.getDateVente())
                .modePaiement(vente.getModePaiement()).montantTotal(vente.getMontantTotal())
                .montantPaye(vente.getMontantPaye()).montantRendu(vente.getMontantRendu())
                .montantDu(vente.getMontantDu()).estPayee(vente.estPayee())
                .nomClient(vente.getNomClient()).telephoneClient(vente.getTelephoneClient())
                .emailClient(vente.getEmailClient()).notes(vente.getNotes())
                .caissierId(vente.getCaissier() != null ? vente.getCaissier().getId() : null)
                .caissierNom(vente.getCaissier() != null ? vente.getCaissier().getNom() : null)
                .lignesVente(lignes).build();
    }

    private ProduitResponse mapProduitToResponse(Produit p) {
        return ProduitResponse.builder().id(p.getId()).codeProduit(p.getCodeProduit()).nom(p.getNom())
                .categorie(p.getCategorie()).prixVente(p.getPrixVente()).stockDisponible(p.getStockDisponible())
                .build();
    }
}