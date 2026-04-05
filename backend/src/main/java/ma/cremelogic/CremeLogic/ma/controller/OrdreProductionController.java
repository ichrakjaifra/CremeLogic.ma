package ma.cremelogic.CremeLogic.ma.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.cremelogic.CremeLogic.ma.dto.request.ExecutionProductionRequest;
import ma.cremelogic.CremeLogic.ma.dto.request.OrdreProductionRequest;
import ma.cremelogic.CremeLogic.ma.dto.response.ApiResponse;
import ma.cremelogic.CremeLogic.ma.dto.response.OrdreProductionResponse;
import ma.cremelogic.CremeLogic.ma.service.interfaces.OrdreProductionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/ordres-production")
@RequiredArgsConstructor
@Tag(name = "Ordres de Production", description = "Gestion des ordres de production")
@SecurityRequirement(name = "Bearer Authentication")
public class OrdreProductionController {

    private final OrdreProductionService ordreProductionService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF')")
    @Operation(summary = "Créer un nouvel ordre de production")
    public ResponseEntity<ApiResponse<OrdreProductionResponse>> createOrdre(
            @Valid @RequestBody OrdreProductionRequest request) {

        OrdreProductionResponse response = ordreProductionService.createOrdre(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<OrdreProductionResponse>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.CREATED.value())
                        .message("Ordre de production créé avec succès")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF')")
    @Operation(summary = "Mettre à jour un ordre de production")
    public ResponseEntity<ApiResponse<OrdreProductionResponse>> updateOrdre(
            @PathVariable Long id,
            @Valid @RequestBody OrdreProductionRequest request) {

        OrdreProductionResponse response = ordreProductionService.updateOrdre(id, request);

        return ResponseEntity.ok(
                ApiResponse.<OrdreProductionResponse>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Ordre de production mis à jour avec succès")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF', 'MAGASINIER', 'EMPLOYE')")
    @Operation(summary = "Récupérer un ordre de production par ID")
    public ResponseEntity<ApiResponse<OrdreProductionResponse>> getOrdre(@PathVariable Long id) {
        OrdreProductionResponse response = ordreProductionService.getOrdre(id);

        return ResponseEntity.ok(
                ApiResponse.<OrdreProductionResponse>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Ordre de production récupéré avec succès")
                        .data(response)
                        .build()
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF', 'MAGASINIER', 'EMPLOYE')")
    @Operation(summary = "Lister tous les ordres de production")
    public ResponseEntity<ApiResponse<List<OrdreProductionResponse>>> getAllOrdres() {
        List<OrdreProductionResponse> response = ordreProductionService.getAllOrdres();

        return ResponseEntity.ok(
                ApiResponse.<List<OrdreProductionResponse>>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Ordres de production récupérés avec succès")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/produit/{produitId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF', 'MAGASINIER', 'EMPLOYE')")
    @Operation(summary = "Lister les ordres par produit")
    public ResponseEntity<ApiResponse<List<OrdreProductionResponse>>> getOrdresByProduit(
            @PathVariable Long produitId) {

        List<OrdreProductionResponse> response = ordreProductionService.getOrdresByProduit(produitId);

        return ResponseEntity.ok(
                ApiResponse.<List<OrdreProductionResponse>>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Ordres par produit récupérés avec succès")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/statut/{statut}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF', 'MAGASINIER', 'EMPLOYE')")
    @Operation(summary = "Lister les ordres par statut")
    public ResponseEntity<ApiResponse<List<OrdreProductionResponse>>> getOrdresByStatut(
            @PathVariable String statut) {

        List<OrdreProductionResponse> response = ordreProductionService.getOrdresByStatut(statut);

        return ResponseEntity.ok(
                ApiResponse.<List<OrdreProductionResponse>>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Ordres par statut récupérés avec succès")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF')")
    @Operation(summary = "Supprimer un ordre de production")
    public ResponseEntity<ApiResponse<Void>> deleteOrdre(@PathVariable Long id) {
        ordreProductionService.deleteOrdre(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Ordre de production supprimé avec succès")
                        .build()
        );
    }

    @PatchMapping("/{id}/statut")
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF')")
    @Operation(summary = "Changer le statut d'un ordre")
    public ResponseEntity<ApiResponse<OrdreProductionResponse>> changerStatut(
            @PathVariable Long id,
            @RequestParam String statut) {

        OrdreProductionResponse response = ordreProductionService.changerStatut(id, statut);

        return ResponseEntity.ok(
                ApiResponse.<OrdreProductionResponse>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Statut modifié avec succès")
                        .data(response)
                        .build()
        );
    }

    @PostMapping("/{id}/demarrer")
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF', 'EMPLOYE')")
    @Operation(summary = "Démarrer une production")
    public ResponseEntity<ApiResponse<OrdreProductionResponse>> demarrerProduction(
            @PathVariable Long id,
            @Valid @RequestBody ExecutionProductionRequest request) {

        OrdreProductionResponse response = ordreProductionService.demarrerProduction(id, request);

        return ResponseEntity.ok(
                ApiResponse.<OrdreProductionResponse>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Production démarrée avec succès")
                        .data(response)
                        .build()
        );
    }

    @PostMapping("/{id}/terminer")
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF', 'EMPLOYE')")
    @Operation(summary = "Terminer une production")
    public ResponseEntity<ApiResponse<OrdreProductionResponse>> terminerProduction(
            @PathVariable Long id,
            @Valid @RequestBody ExecutionProductionRequest request) {

        OrdreProductionResponse response = ordreProductionService.terminerProduction(id, request);

        return ResponseEntity.ok(
                ApiResponse.<OrdreProductionResponse>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Production terminée avec succès")
                        .data(response)
                        .build()
        );
    }

    @PatchMapping("/{id}/etapes/{suiviEtapeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF', 'EMPLOYE')")
    @Operation(summary = "Mettre à jour le statut d'une étape de production")
    public ResponseEntity<ApiResponse<OrdreProductionResponse>> updateStatutEtape(
            @PathVariable Long id,
            @PathVariable Long suiviEtapeId,
            @RequestParam String statut) {

        OrdreProductionResponse response = ordreProductionService.updateStatutEtape(id, suiviEtapeId, statut);

        return ResponseEntity.ok(
                ApiResponse.<OrdreProductionResponse>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Statut de l'étape mis à jour avec succès")
                        .data(response)
                        .build()
        );
    }

    @PostMapping("/{id}/annuler")
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF')")
    @Operation(summary = "Annuler une production")
    public ResponseEntity<ApiResponse<OrdreProductionResponse>> annulerProduction(
            @PathVariable Long id,
            @RequestParam String raison) {

        OrdreProductionResponse response = ordreProductionService.annulerProduction(id, raison);

        return ResponseEntity.ok(
                ApiResponse.<OrdreProductionResponse>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Production annulée avec succès")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/en-retard")
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF')")
    @Operation(summary = "Lister les ordres en retard")
    public ResponseEntity<ApiResponse<List<OrdreProductionResponse>>> getOrdresEnRetard() {
        List<OrdreProductionResponse> response = ordreProductionService.getOrdresEnRetard();

        return ResponseEntity.ok(
                ApiResponse.<List<OrdreProductionResponse>>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Ordres en retard récupérés avec succès")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/cout-periode")
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF', 'EMPLOYE')")
    @Operation(summary = "Coût total de production sur une période")
    public ResponseEntity<ApiResponse<BigDecimal>> getCoutTotalProductionPeriode(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {

        BigDecimal response = ordreProductionService.getCoutTotalProductionPeriode(debut, fin);

        return ResponseEntity.ok(
                ApiResponse.<BigDecimal>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Coût total de production récupéré avec succès")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/quantite-produite")
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF')")
    @Operation(summary = "Quantité produite d'un produit sur une période")
    public ResponseEntity<ApiResponse<Integer>> getQuantiteProduite(
            @RequestParam Long produitId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {

        Integer response = ordreProductionService.getQuantiteProduite(produitId, debut, fin);

        return ResponseEntity.ok(
                ApiResponse.<Integer>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Quantité produite récupérée avec succès")
                        .data(response)
                        .build()
        );
    }

    @PostMapping("/{id}/dupliquer")
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF')")
    @Operation(summary = "Dupliquer un ordre de production")
    public ResponseEntity<ApiResponse<OrdreProductionResponse>> dupliquerOrdre(@PathVariable Long id) {
        OrdreProductionResponse response = ordreProductionService.dupliquerOrdre(id);

        return ResponseEntity.ok(
                ApiResponse.<OrdreProductionResponse>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Ordre dupliqué avec succès")
                        .data(response)
                        .build()
        );
    }

    @PostMapping("/{id}/consommer-ingredients")
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF')")
    @Operation(summary = "Forcer la consommation des ingrédients pour un ordre")
    public ResponseEntity<ApiResponse<Void>> consommerIngredients(@PathVariable Long id) {
        ordreProductionService.consommerIngredients(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Consommation des ingrédients effectuée avec succès")
                        .build()
        );
    }
}