package ma.cremelogic.CremeLogic.ma.controller;

import ma.cremelogic.CremeLogic.ma.dto.request.RecetteRequest;
import ma.cremelogic.CremeLogic.ma.dto.response.ApiResponse;
import ma.cremelogic.CremeLogic.ma.dto.response.RecetteResponse;
import ma.cremelogic.CremeLogic.ma.service.interfaces.RecetteService;
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
@RequestMapping("/api/recettes")
@RequiredArgsConstructor
@Tag(name = "Recettes", description = "Gestion des recettes de pâtisserie")
public class RecetteController {

    private final RecetteService recetteService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('CHEF')")
    @Operation(summary = "Créer une nouvelle recette")
    public ResponseEntity<ApiResponse<RecetteResponse>> createRecette(@Valid @RequestBody RecetteRequest request) {
        RecetteResponse response = recetteService.createRecette(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<RecetteResponse>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.CREATED.value())
                        .message("Recette créée avec succès")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CHEF')")
    @Operation(summary = "Mettre à jour une recette")
    public ResponseEntity<ApiResponse<RecetteResponse>> updateRecette(
            @PathVariable Long id,
            @Valid @RequestBody RecetteRequest request) {
        RecetteResponse response = recetteService.updateRecette(id, request);
        return ResponseEntity.ok(
                ApiResponse.<RecetteResponse>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Recette mise à jour avec succès")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF', 'MAGASINIER', 'EMPLOYE')")
    @Operation(summary = "Récupérer une recette par ID")
    public ResponseEntity<ApiResponse<RecetteResponse>> getRecette(@PathVariable Long id) {
        RecetteResponse response = recetteService.getRecette(id);
        return ResponseEntity.ok(
                ApiResponse.<RecetteResponse>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Recette récupérée avec succès")
                        .data(response)
                        .build()
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF', 'MAGASINIER', 'EMPLOYE')")
    @Operation(summary = "Lister toutes les recettes")
    public ResponseEntity<ApiResponse<List<RecetteResponse>>> getAllRecettes() {
        List<RecetteResponse> response = recetteService.getAllRecettes();
        return ResponseEntity.ok(
                ApiResponse.<List<RecetteResponse>>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Recettes récupérées avec succès")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/createur/{createurId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF')")
    @Operation(summary = "Lister les recettes par créateur")
    public ResponseEntity<ApiResponse<List<RecetteResponse>>> getRecettesByCreateur(@PathVariable Long createurId) {
        List<RecetteResponse> response = recetteService.getRecettesByCreateur(createurId);
        return ResponseEntity.ok(
                ApiResponse.<List<RecetteResponse>>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Recettes par créateur récupérées avec succès")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF', 'MAGASINIER', 'EMPLOYE')")
    @Operation(summary = "Rechercher des recettes")
    public ResponseEntity<ApiResponse<List<RecetteResponse>>> searchRecettes(@RequestParam String keyword) {
        List<RecetteResponse> response = recetteService.searchRecettes(keyword);
        return ResponseEntity.ok(
                ApiResponse.<List<RecetteResponse>>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Recettes recherchées avec succès")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CHEF')")
    @Operation(summary = "Supprimer une recette")
    public ResponseEntity<ApiResponse<Void>> deleteRecette(@PathVariable Long id) {
        recetteService.deleteRecette(id);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Recette supprimée avec succès")
                        .build()
        );
    }

    @PostMapping("/{id}/dupliquer")
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF')")
    @Operation(summary = "Dupliquer une recette")
    public ResponseEntity<ApiResponse<RecetteResponse>> dupliquerRecette(
            @PathVariable Long id,
            @RequestParam(required = false) String nouveauNom) {
        RecetteResponse response = recetteService.dupliquerRecette(id, nouveauNom);
        return ResponseEntity.ok(
                ApiResponse.<RecetteResponse>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Recette dupliquée avec succès")
                        .data(response)
                        .build()
        );
    }

    @PostMapping("/{id}/calculer-cout")
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF')")
    @Operation(summary = "Recalculer le coût d'une recette")
    public ResponseEntity<ApiResponse<Map<String, BigDecimal>>> calculerCoutRecette(@PathVariable Long id) {
        BigDecimal cout = recetteService.calculerCoutRecette(id);
        return ResponseEntity.ok(
                ApiResponse.<Map<String, BigDecimal>>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Coût de la recette calculé avec succès")
                        .data(Map.of("coutTotal", cout))
                        .build()
        );
    }

    @GetMapping("/ingredient/{ingredientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF')")
    @Operation(summary = "Lister les recettes utilisant un ingrédient")
    public ResponseEntity<ApiResponse<List<RecetteResponse>>> getRecettesByIngredient(@PathVariable Long ingredientId) {
        List<RecetteResponse> response = recetteService.getRecettesByIngredient(ingredientId);
        return ResponseEntity.ok(
                ApiResponse.<List<RecetteResponse>>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Recettes par ingrédient récupérées avec succès")
                        .data(response)
                        .build()
        );
    }

    @PostMapping("/{id}/ajuster-portions")
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF')")
    @Operation(summary = "Ajuster les portions d'une recette")
    public ResponseEntity<ApiResponse<RecetteResponse>> ajusterPortions(
            @PathVariable Long id,
            @RequestParam Integer nouvellesPortions) {
        RecetteResponse response = recetteService.ajusterPortions(id, nouvellesPortions);
        return ResponseEntity.ok(
                ApiResponse.<RecetteResponse>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Portions ajustées avec succès")
                        .data(response)
                        .build()
        );
    }
}