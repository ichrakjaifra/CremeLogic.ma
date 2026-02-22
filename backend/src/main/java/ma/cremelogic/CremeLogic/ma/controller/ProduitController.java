package ma.cremelogic.CremeLogic.ma.controller;

import ma.cremelogic.CremeLogic.ma.dto.request.ProduitRequest;
import ma.cremelogic.CremeLogic.ma.dto.response.ApiResponse;
import ma.cremelogic.CremeLogic.ma.dto.response.ProduitResponse;
import ma.cremelogic.CremeLogic.ma.service.interfaces.ProduitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/produits")
@RequiredArgsConstructor
@Tag(name = "Produits", description = "Gestion des produits finis")
public class ProduitController {

    private final ProduitService produitService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('CHEF')")
    @Operation(summary = "Créer un nouveau produit")
    public ResponseEntity<ApiResponse<ProduitResponse>> createProduit(@Valid @RequestBody ProduitRequest request) {
        ProduitResponse response = produitService.createProduit(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<ProduitResponse>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.CREATED.value())
                        .message("Produit créé avec succès")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CHEF')")
    @Operation(summary = "Mettre à jour un produit")
    public ResponseEntity<ApiResponse<ProduitResponse>> updateProduit(
            @PathVariable Long id,
            @Valid @RequestBody ProduitRequest request) {
        ProduitResponse response = produitService.updateProduit(id, request);
        return ResponseEntity.ok(
                ApiResponse.<ProduitResponse>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Produit mis à jour avec succès")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF', 'MAGASINIER', 'EMPLOYE')")
    @Operation(summary = "Récupérer un produit par ID")
    public ResponseEntity<ApiResponse<ProduitResponse>> getProduit(@PathVariable Long id) {
        ProduitResponse response = produitService.getProduit(id);
        return ResponseEntity.ok(
                ApiResponse.<ProduitResponse>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Produit récupéré avec succès")
                        .data(response)
                        .build()
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF', 'MAGASINIER', 'EMPLOYE')")
    @Operation(summary = "Lister tous les produits")
    public ResponseEntity<ApiResponse<List<ProduitResponse>>> getAllProduits() {
        List<ProduitResponse> response = produitService.getAllProduits();
        return ResponseEntity.ok(
                ApiResponse.<List<ProduitResponse>>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Produits récupérés avec succès")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/categorie/{categorie}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF', 'MAGASINIER', 'EMPLOYE')")
    @Operation(summary = "Lister les produits par catégorie")
    public ResponseEntity<ApiResponse<List<ProduitResponse>>> getProduitsByCategorie(@PathVariable String categorie) {
        List<ProduitResponse> response = produitService.getProduitsByCategorie(categorie);
        return ResponseEntity.ok(
                ApiResponse.<List<ProduitResponse>>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Produits par catégorie récupérés avec succès")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/stock-faible")
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF', 'MAGASINIER')")
    @Operation(summary = "Lister les produits en stock faible")
    public ResponseEntity<ApiResponse<List<ProduitResponse>>> getProduitsStockFaible() {
        List<ProduitResponse> response = produitService.getProduitsStockFaible();
        return ResponseEntity.ok(
                ApiResponse.<List<ProduitResponse>>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Produits en stock faible récupérés avec succès")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF', 'MAGASINIER', 'EMPLOYE')")
    @Operation(summary = "Rechercher des produits")
    public ResponseEntity<ApiResponse<List<ProduitResponse>>> searchProduits(@RequestParam String keyword) {
        List<ProduitResponse> response = produitService.searchProduits(keyword);
        return ResponseEntity.ok(
                ApiResponse.<List<ProduitResponse>>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Produits recherchés avec succès")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Supprimer un produit")
    public ResponseEntity<ApiResponse<Void>> deleteProduit(@PathVariable Long id) {
        produitService.deleteProduit(id);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Produit supprimé avec succès")
                        .build()
        );
    }

    @PostMapping("/{id}/ajuster-stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAGASINIER')")
    @Operation(summary = "Ajuster le stock d'un produit")
    public ResponseEntity<ApiResponse<ProduitResponse>> ajusterStock(
            @PathVariable Long id,
            @RequestParam Integer quantite,
            @RequestParam String type,
            @RequestParam(required = false) String raison) {
        ProduitResponse response = produitService.ajusterStock(id, quantite, type);
        return ResponseEntity.ok(
                ApiResponse.<ProduitResponse>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Stock ajusté avec succès")
                        .data(response)
                        .build()
        );
    }

    @PostMapping("/{produitId}/lier-recette/{recetteId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF')")
    @Operation(summary = "Lier une recette à un produit")
    public ResponseEntity<ApiResponse<ProduitResponse>> lierRecette(
            @PathVariable Long produitId,
            @PathVariable Long recetteId) {
        ProduitResponse response = produitService.lierRecette(produitId, recetteId);
        return ResponseEntity.ok(
                ApiResponse.<ProduitResponse>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Recette liée avec succès")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/plus-vendus")
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF')")
    @Operation(summary = "Récupérer les produits les plus vendus")
    public ResponseEntity<ApiResponse<List<ProduitResponse>>> getProduitsPlusVendus(
            @RequestParam(defaultValue = "10") int limit) {
        List<ProduitResponse> response = produitService.getProduitsPlusVendus(limit);
        return ResponseEntity.ok(
                ApiResponse.<List<ProduitResponse>>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Produits les plus vendus récupérés avec succès")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/valeur-stock-total")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAGASINIER')")
    @Operation(summary = "Récupérer la valeur totale du stock de produits")
    public ResponseEntity<ApiResponse<Map<String, BigDecimal>>> getValeurStockTotal() {
        BigDecimal valeur = produitService.getValeurStockTotal();
        return ResponseEntity.ok(
                ApiResponse.<Map<String, BigDecimal>>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Valeur du stock total récupérée avec succès")
                        .data(Map.of("valeurStockTotal", valeur))
                        .build()
        );
    }
}