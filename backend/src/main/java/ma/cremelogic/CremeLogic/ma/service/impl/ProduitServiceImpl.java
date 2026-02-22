package ma.cremelogic.CremeLogic.ma.service.impl;


import ma.cremelogic.CremeLogic.ma.dto.request.ProduitRequest;
import ma.cremelogic.CremeLogic.ma.dto.response.ProduitResponse;
import ma.cremelogic.CremeLogic.ma.entity.Produit;
import ma.cremelogic.CremeLogic.ma.entity.Recette;
import ma.cremelogic.CremeLogic.ma.enums.CategorieProduit;
import ma.cremelogic.CremeLogic.ma.enums.StatutProduit;
import ma.cremelogic.CremeLogic.ma.exception.ResourceNotFoundException;
import ma.cremelogic.CremeLogic.ma.exception.ValidationException;
import ma.cremelogic.CremeLogic.ma.repository.ProduitRepository;
import ma.cremelogic.CremeLogic.ma.repository.RecetteRepository;
import ma.cremelogic.CremeLogic.ma.service.ProduitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProduitServiceImpl implements ProduitService {

    private final ProduitRepository produitRepository;
    private final RecetteRepository recetteRepository;
    private final HistoriqueService historiqueService;

    @Override
    @Transactional
    public ProduitResponse createProduit(ProduitRequest request) {
        // Validation
        if (request.getStockMinimum() > request.getStockMaximum()) {
            throw new ValidationException("Le stock minimum ne peut pas être supérieur au stock maximum");
        }

        // Vérifier si le code produit existe déjà
        if (produitRepository.existsByCodeProduit(generateCodeProduit(request.getNom()))) {
            throw new ValidationException("Un produit avec un code similaire existe déjà");
        }

        // Créer le produit
        Produit produit = Produit.builder()
                .nom(request.getNom())
                .codeProduit(generateCodeProduit(request.getNom()))
                .description(request.getDescription())
                .categorie(request.getCategorie())
                .prixVente(request.getPrixVente())
                .stockMinimum(request.getStockMinimum())
                .stockMaximum(request.getStockMaximum())
                .stockDisponible(0)
                .statut(StatutProduit.ACTIF)
                .imageUrl(request.getImageUrl())
                .build();

        // Lier la recette si fournie
        if (request.getRecetteId() != null) {
            Recette recette = recetteRepository.findById(request.getRecetteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Recette", "id", request.getRecetteId()));
            produit.setRecette(recette);
            calculerCoutProduction(produit);
        }

        Produit savedProduit = produitRepository.save(produit);

        // Historique
        historiqueService.enregistrerCreation("PRODUIT", savedProduit.getId(),
                "Création du produit: " + savedProduit.getNom());

        log.info("Produit créé: {}", savedProduit.getCodeProduit());
        return mapToResponse(savedProduit);
    }

    @Override
    @Transactional
    public ProduitResponse updateProduit(Long id, ProduitRequest request) {
        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit", "id", id));

        // Validation
        if (request.getStockMinimum() > request.getStockMaximum()) {
            throw new ValidationException("Le stock minimum ne peut pas être supérieur au stock maximum");
        }

        // Sauvegarder anciennes valeurs pour historique
        String ancienNom = produit.getNom();
        BigDecimal ancienPrix = produit.getPrixVente();

        // Mettre à jour
        produit.setNom(request.getNom());
        produit.setDescription(request.getDescription());
        produit.setCategorie(request.getCategorie());
        produit.setPrixVente(request.getPrixVente());
        produit.setStockMinimum(request.getStockMinimum());
        produit.setStockMaximum(request.getStockMaximum());
        produit.setImageUrl(request.getImageUrl());
        produit.setStatut(request.getStatut() != null ? request.getStatut() : produit.getStatut());

        // Vérifier si la recette a changé
        if (request.getRecetteId() != null &&
                (produit.getRecette() == null || !produit.getRecette().getId().equals(request.getRecetteId()))) {
            Recette recette = recetteRepository.findById(request.getRecetteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Recette", "id", request.getRecetteId()));
            produit.setRecette(recette);
            calculerCoutProduction(produit);
        }

        // Mettre à jour le statut basé sur le stock
        updateStatutBasedOnStock(produit);

        Produit updatedProduit = produitRepository.save(produit);

        // Historique
        historiqueService.enregistrerModification("PRODUIT", id,
                String.format("Mise à jour: %s -> %s, Prix: %s -> %s",
                        ancienNom, produit.getNom(), ancienPrix, produit.getPrixVente()));

        log.info("Produit mis à jour: {}", produit.getCodeProduit());
        return mapToResponse(updatedProduit);
    }

    @Override
    public ProduitResponse getProduit(Long id) {
        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit", "id", id));
        return mapToResponse(produit);
    }

    @Override
    public List<ProduitResponse> getAllProduits() {
        return produitRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<ProduitResponse> getProduitsByCategorie(String categorie) {
        try {
            CategorieProduit categorieEnum = CategorieProduit.valueOf(categorie.toUpperCase());
            return produitRepository.findByCategorie(categorieEnum).stream()
                    .map(this::mapToResponse)
                    .toList();
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Catégorie invalide: " + categorie);
        }
    }

    @Override
    public List<ProduitResponse> getProduitsStockFaible() {
        return produitRepository.findProduitsStockFaible().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<ProduitResponse> searchProduits(String keyword) {
        return produitRepository.findByNomContainingIgnoreCase(keyword).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public void deleteProduit(Long id) {
        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit", "id", id));

        // Vérifier si le produit a des ventes ou des productions
        if (!produit.getLignesVente().isEmpty()) {
            throw new ValidationException("Impossible de supprimer un produit avec des ventes associées");
        }
        if (!produit.getOrdresProduction().isEmpty()) {
            throw new ValidationException("Impossible de supprimer un produit avec des productions associées");
        }

        produitRepository.delete(produit);
        historiqueService.enregistrerSuppression("PRODUIT", id, "Suppression du produit: " + produit.getNom());
        log.info("Produit supprimé: {}", produit.getCodeProduit());
    }

    @Override
    @Transactional
    public ProduitResponse ajusterStock(Long id, Integer quantite, String type) {
        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit", "id", id));

        int ancienStock = produit.getStockDisponible();
        int nouveauStock;

        switch (type.toUpperCase()) {
            case "ENTREE" -> {
                nouveauStock = ancienStock + quantite;
                produit.setStockDisponible(nouveauStock);
                historiqueService.enregistrerModification("PRODUIT", id,
                        String.format("Entrée stock: %d -> %d (+%d)", ancienStock, nouveauStock, quantite));
            }
            case "SORTIE" -> {
                if (quantite > ancienStock) {
                    throw new ValidationException("Stock insuffisant. Disponible: " + ancienStock);
                }
                nouveauStock = ancienStock - quantite;
                produit.setStockDisponible(nouveauStock);
                historiqueService.enregistrerModification("PRODUIT", id,
                        String.format("Sortie stock: %d -> %d (-%d)", ancienStock, nouveauStock, quantite));
            }
            case "AJUSTEMENT" -> {
                produit.setStockDisponible(quantite);
                historiqueService.enregistrerModification("PRODUIT", id,
                        String.format("Ajustement stock: %d -> %d", ancienStock, quantite));
            }
            default -> throw new ValidationException("Type d'ajustement invalide: " + type);
        }

        // Mettre à jour le statut
        updateStatutBasedOnStock(produit);

        Produit updatedProduit = produitRepository.save(produit);
        return mapToResponse(updatedProduit);
    }

    @Override
    @Transactional
    public ProduitResponse lierRecette(Long produitId, Long recetteId) {
        Produit produit = produitRepository.findById(produitId)
                .orElseThrow(() -> new ResourceNotFoundException("Produit", "id", produitId));
        Recette recette = recetteRepository.findById(recetteId)
                .orElseThrow(() -> new ResourceNotFoundException("Recette", "id", recetteId));

        produit.setRecette(recette);
        calculerCoutProduction(produit);

        Produit updatedProduit = produitRepository.save(produit);

        historiqueService.enregistrerModification("PRODUIT", produitId,
                "Liaison avec recette: " + recette.getNom());

        return mapToResponse(updatedProduit);
    }

    @Override
    @Transactional
    public void calculerCoutProduction(Long produitId) {
        Produit produit = produitRepository.findById(produitId)
                .orElseThrow(() -> new ResourceNotFoundException("Produit", "id", produitId));

        if (produit.getRecette() != null) {
            BigDecimal coutProduction = produit.getRecette().getCoutTotal();
            produit.setCoutProduction(coutProduction);
            produitRepository.save(produit);

            log.info("Coût de production calculé pour {}: {}", produit.getNom(), coutProduction);
        }
    }

    @Override
    public List<ProduitResponse> getProduitsPlusVendus(int limit) {
        // Implémentation simplifiée - À compléter avec une requête complexe
        return produitRepository.findRecentProducts(limit).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public BigDecimal getValeurStockTotal() {
        return produitRepository.findAll().stream()
                .map(p -> p.getPrixVente().multiply(new BigDecimal(p.getStockDisponible())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Méthodes privées
    private String generateCodeProduit(String nom) {
        String code = "PROD-" + nom.substring(0, Math.min(3, nom.length())).toUpperCase() +
                "-" + System.currentTimeMillis() % 10000;
        return code;
    }

    private void updateStatutBasedOnStock(Produit produit) {
        if (produit.getStockDisponible() <= 0) {
            produit.setStatut(StatutProduit.RUPTURE_STOCK);
        } else if (produit.getStockDisponible() <= produit.getStockMinimum()) {
            produit.setStatut(StatutProduit.ACTIF); // Ou créer un statut STOCK_FAIBLE
        } else {
            produit.setStatut(StatutProduit.ACTIF);
        }
    }

    private void calculerCoutProduction(Produit produit) {
        if (produit.getRecette() != null) {
            produit.getRecette().calculerCoutTotal();
            produit.setCoutProduction(produit.getRecette().getCoutTotal());
        }
    }

    private ProduitResponse mapToResponse(Produit produit) {
        return ProduitResponse.builder()
                .id(produit.getId())
                .codeProduit(produit.getCodeProduit())
                .nom(produit.getNom())
                .description(produit.getDescription())
                .categorie(produit.getCategorie())
                .prixVente(produit.getPrixVente())
                .coutProduction(produit.getCoutProduction())
                .marge(produit.getMarge())
                .statut(produit.getStatut())
                .stockDisponible(produit.getStockDisponible())
                .stockMinimum(produit.getStockMinimum())
                .stockMaximum(produit.getStockMaximum())
                .recetteId(produit.getRecette() != null ? produit.getRecette().getId() : null)
                .imageUrl(produit.getImageUrl())
                .dateCreation(produit.getDateCreation())
                .dateModification(produit.getDateModification())
                .stockFaible(produit.getStockDisponible() <= produit.getStockMinimum())
                .enRupture(produit.getStockDisponible() == 0)
                .valeurStock(produit.getPrixVente().multiply(new BigDecimal(produit.getStockDisponible())))
                .build();
    }
}
