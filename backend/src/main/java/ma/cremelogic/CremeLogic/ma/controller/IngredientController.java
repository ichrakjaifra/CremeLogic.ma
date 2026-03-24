package ma.cremelogic.CremeLogic.ma.controller;

import ma.cremelogic.CremeLogic.ma.dto.request.IngredientRequest;
import ma.cremelogic.CremeLogic.ma.dto.response.ApiResponse;
import ma.cremelogic.CremeLogic.ma.dto.response.IngredientResponse;
import ma.cremelogic.CremeLogic.ma.service.interfaces.IngredientService;
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
@RequestMapping("/ingredients")
@RequiredArgsConstructor
@Tag(name = "Ingrédients", description = "Gestion des ingrédients/matières premières")
public class IngredientController {

    private final IngredientService ingredientService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MAGASINIER')")
    @Operation(summary = "Créer un nouvel ingrédient")
    public ResponseEntity<ApiResponse<IngredientResponse>> createIngredient(@Valid @RequestBody IngredientRequest request) {
        IngredientResponse response = ingredientService.createIngredient(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<IngredientResponse>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.CREATED.value())
                        .message("Ingrédient créé avec succès")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MAGASINIER')")
    @Operation(summary = "Mettre à jour un ingrédient")
    public ResponseEntity<ApiResponse<IngredientResponse>> updateIngredient(
            @PathVariable Long id,
            @Valid @RequestBody IngredientRequest request) {
        IngredientResponse response = ingredientService.updateIngredient(id, request);
        return ResponseEntity.ok(
                ApiResponse.<IngredientResponse>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Ingrédient mis à jour avec succès")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF', 'MAGASINIER', 'EMPLOYE')")
    @Operation(summary = "Récupérer un ingrédient par ID")
    public ResponseEntity<ApiResponse<IngredientResponse>> getIngredient(@PathVariable Long id) {
        IngredientResponse response = ingredientService.getIngredient(id);
        return ResponseEntity.ok(
                ApiResponse.<IngredientResponse>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Ingrédient récupéré avec succès")
                        .data(response)
                        .build()
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF', 'MAGASINIER', 'EMPLOYE')")
    @Operation(summary = "Lister tous les ingrédients")
    public ResponseEntity<ApiResponse<List<IngredientResponse>>> getAllIngredients() {
        List<IngredientResponse> response = ingredientService.getAllIngredients();
        return ResponseEntity.ok(
                ApiResponse.<List<IngredientResponse>>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Ingrédients récupérés avec succès")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/stock-faible")
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF', 'MAGASINIER')")
    @Operation(summary = "Lister les ingrédients en stock faible")
    public ResponseEntity<ApiResponse<List<IngredientResponse>>> getIngredientsStockFaible() {
        List<IngredientResponse> response = ingredientService.getIngredientsStockFaible();
        return ResponseEntity.ok(
                ApiResponse.<List<IngredientResponse>>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Ingrédients en stock faible récupérés avec succès")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/expirant")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAGASINIER')")
    @Operation(summary = "Lister les ingrédients expirant bientôt")
    public ResponseEntity<ApiResponse<List<IngredientResponse>>> getIngredientsExpirant() {
        List<IngredientResponse> response = ingredientService.getIngredientsExpirant();
        return ResponseEntity.ok(
                ApiResponse.<List<IngredientResponse>>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Ingrédients expirant bientôt récupérés avec succès")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF', 'MAGASINIER', 'EMPLOYE')")
    @Operation(summary = "Rechercher des ingrédients")
    public ResponseEntity<ApiResponse<List<IngredientResponse>>> searchIngredients(@RequestParam String keyword) {
        List<IngredientResponse> response = ingredientService.searchIngredients(keyword);
        return ResponseEntity.ok(
                ApiResponse.<List<IngredientResponse>>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Ingrédients recherchés avec succès")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAGASINIER')")
    @Operation(summary = "Supprimer un ingrédient")
    public ResponseEntity<ApiResponse<Void>> deleteIngredient(@PathVariable Long id) {
        ingredientService.deleteIngredient(id);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Ingrédient supprimé avec succès")
                        .build()
        );
    }

    @PostMapping("/{id}/ajuster-stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAGASINIER')")
    @Operation(summary = "Ajuster le stock d'un ingrédient")
    public ResponseEntity<ApiResponse<IngredientResponse>> ajusterStock(
            @PathVariable Long id,
            @RequestParam BigDecimal quantite,
            @RequestParam String type,
            @RequestParam(required = false) String raison) {
        IngredientResponse response = ingredientService.ajusterStock(id, quantite, type, raison);
        return ResponseEntity.ok(
                ApiResponse.<IngredientResponse>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Stock ajusté avec succès")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{id}/consommation-moyenne")
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF', 'MAGASINIER')")
    @Operation(summary = "Récupérer la consommation moyenne d'un ingrédient")
    public ResponseEntity<ApiResponse<Map<String, BigDecimal>>> getConsommationMoyenne(
            @PathVariable Long id,
            @RequestParam(defaultValue = "30") int jours) {
        BigDecimal consommation = ingredientService.getConsommationMoyenne(id, jours);
        return ResponseEntity.ok(
                ApiResponse.<Map<String, BigDecimal>>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Consommation moyenne récupérée avec succès")
                        .data(Map.of("consommationMoyenne", consommation))
                        .build()
        );
    }

    @GetMapping("/valeur-stock-total")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAGASINIER')")
    @Operation(summary = "Récupérer la valeur totale du stock d'ingrédients")
    public ResponseEntity<ApiResponse<Map<String, BigDecimal>>> getValeurStockTotal() {
        BigDecimal valeur = ingredientService.getValeurStockTotal();
        return ResponseEntity.ok(
                ApiResponse.<Map<String, BigDecimal>>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Valeur du stock total récupérée avec succès")
                        .data(Map.of("valeurStockTotal", valeur))
                        .build()
        );
    }

    @GetMapping("/fournisseur/{fournisseurId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAGASINIER')")
    @Operation(summary = "Lister les ingrédients par fournisseur")
    public ResponseEntity<ApiResponse<List<IngredientResponse>>> getIngredientsParFournisseur(
            @PathVariable Long fournisseurId) {
        List<IngredientResponse> response = ingredientService.getIngredientsParFournisseur(fournisseurId);
        return ResponseEntity.ok(
                ApiResponse.<List<IngredientResponse>>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Ingrédients par fournisseur récupérés avec succès")
                        .data(response)
                        .build()
        );
    }
}