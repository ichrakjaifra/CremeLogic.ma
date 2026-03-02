package ma.cremelogic.CremeLogic.ma.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.cremelogic.CremeLogic.ma.dto.request.AjustementStockRequest;
import ma.cremelogic.CremeLogic.ma.dto.response.ApiResponse;
import ma.cremelogic.CremeLogic.ma.dto.response.MouvementStockResponse;
import ma.cremelogic.CremeLogic.ma.enums.TypeMouvement;
import ma.cremelogic.CremeLogic.ma.service.interfaces.MouvementStockService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mouvements-stock")
@RequiredArgsConstructor
@Tag(name = "Mouvements de Stock", description = "Gestion des mouvements de stock (entrées, sorties, ajustements)")
@SecurityRequirement(name = "Bearer Authentication")
public class MouvementStockController {

    private final MouvementStockService mouvementStockService;

    @PostMapping("/entree")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAGASINIER')")
    @Operation(summary = "Enregistrer une entrée de stock")
    public ResponseEntity<ApiResponse<MouvementStockResponse>> enregistrerEntree(
            @RequestParam Long ingredientId,
            @RequestParam BigDecimal quantite,
            @RequestParam(required = false) BigDecimal coutUnitaire,
            @RequestParam String raison,
            @RequestParam(required = false) Long commandeId) {

        MouvementStockResponse response = mouvementStockService.enregistrerEntree(
                ingredientId, quantite, coutUnitaire, raison, commandeId);

        return ResponseEntity.ok(ApiResponse.<MouvementStockResponse>builder()
                .timestamp(LocalDateTime.now())
                .status(200)
                .message("Entrée de stock enregistrée avec succès")
                .data(response)
                .build());
    }

    @PostMapping("/sortie")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAGASINIER')")
    @Operation(summary = "Enregistrer une sortie de stock")
    public ResponseEntity<ApiResponse<MouvementStockResponse>> enregistrerSortie(
            @RequestParam Long ingredientId,
            @RequestParam BigDecimal quantite,
            @RequestParam String raison,
            @RequestParam(required = false) Long ordreProductionId) {

        MouvementStockResponse response = mouvementStockService.enregistrerSortie(
                ingredientId, quantite, raison, ordreProductionId);

        return ResponseEntity.ok(ApiResponse.<MouvementStockResponse>builder()
                .timestamp(LocalDateTime.now())
                .status(200)
                .message("Sortie de stock enregistrée avec succès")
                .data(response)
                .build());
    }

    @PostMapping("/perte")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAGASINIER')")
    @Operation(summary = "Enregistrer une perte de stock")
    public ResponseEntity<ApiResponse<MouvementStockResponse>> enregistrerPerte(
            @RequestParam Long ingredientId,
            @RequestParam BigDecimal quantite,
            @RequestParam String raison) {

        MouvementStockResponse response = mouvementStockService.enregistrerPerte(
                ingredientId, quantite, raison);

        return ResponseEntity.ok(ApiResponse.<MouvementStockResponse>builder()
                .timestamp(LocalDateTime.now())
                .status(200)
                .message("Perte enregistrée avec succès")
                .data(response)
                .build());
    }

    @PostMapping("/ajustement")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAGASINIER')")
    @Operation(summary = "Ajuster le stock d'un ingrédient")
    public ResponseEntity<ApiResponse<MouvementStockResponse>> enregistrerAjustement(
            @RequestParam Long ingredientId,
            @RequestParam BigDecimal nouvelleQuantite,
            @RequestParam String raison) {

        MouvementStockResponse response = mouvementStockService.enregistrerAjustement(
                ingredientId, nouvelleQuantite, raison);

        return ResponseEntity.ok(ApiResponse.<MouvementStockResponse>builder()
                .timestamp(LocalDateTime.now())
                .status(200)
                .message("Ajustement de stock enregistré avec succès")
                .data(response)
                .build());
    }

    @GetMapping("/ingredient/{ingredientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAGASINIER')")
    @Operation(summary = "Récupérer les mouvements d'un ingrédient")
    public ResponseEntity<ApiResponse<List<MouvementStockResponse>>> getMouvementsParIngredient(
            @PathVariable Long ingredientId) {

        List<MouvementStockResponse> response = mouvementStockService.getMouvementsParIngredient(ingredientId);

        return ResponseEntity.ok(ApiResponse.<List<MouvementStockResponse>>builder()
                .timestamp(LocalDateTime.now())
                .status(200)
                .message("Mouvements récupérés avec succès")
                .data(response)
                .build());
    }

    @GetMapping("/periode")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAGASINIER')")
    @Operation(summary = "Récupérer les mouvements par période")
    public ResponseEntity<ApiResponse<List<MouvementStockResponse>>> getMouvementsParPeriode(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {

        List<MouvementStockResponse> response = mouvementStockService.getMouvementsParPeriode(debut, fin);

        return ResponseEntity.ok(ApiResponse.<List<MouvementStockResponse>>builder()
                .timestamp(LocalDateTime.now())
                .status(200)
                .message("Mouvements récupérés avec succès")
                .data(response)
                .build());
    }

    @GetMapping("/type/{type}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAGASINIER')")
    @Operation(summary = "Récupérer les mouvements par type")
    public ResponseEntity<ApiResponse<List<MouvementStockResponse>>> getMouvementsParType(
            @PathVariable TypeMouvement type) {

        List<MouvementStockResponse> response = mouvementStockService.getMouvementsParType(type);

        return ResponseEntity.ok(ApiResponse.<List<MouvementStockResponse>>builder()
                .timestamp(LocalDateTime.now())
                .status(200)
                .message("Mouvements récupérés avec succès")
                .data(response)
                .build());
    }

    @GetMapping("/statistiques/{ingredientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAGASINIER')")
    @Operation(summary = "Récupérer les statistiques de consommation d'un ingrédient")
    public ResponseEntity<ApiResponse<Map<String, BigDecimal>>> getStatistiquesConsommation(
            @PathVariable Long ingredientId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {

        Map<String, BigDecimal> response = mouvementStockService.getStatistiquesConsommation(ingredientId, debut, fin);

        return ResponseEntity.ok(ApiResponse.<Map<String, BigDecimal>>builder()
                .timestamp(LocalDateTime.now())
                .status(200)
                .message("Statistiques récupérées avec succès")
                .data(response)
                .build());
    }

    @GetMapping("/historique/{ingredientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAGASINIER')")
    @Operation(summary = "Récupérer l'historique complet d'un ingrédient")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getHistoriqueComplet(
            @PathVariable Long ingredientId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {

        Map<String, Object> response = mouvementStockService.getHistoriqueComplet(ingredientId, debut, fin);

        return ResponseEntity.ok(ApiResponse.<Map<String, Object>>builder()
                .timestamp(LocalDateTime.now())
                .status(200)
                .message("Historique récupéré avec succès")
                .data(response)
                .build());
    }

    @PostMapping("/verifier-alertes")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Déclencher la vérification manuelle des alertes")
    public ResponseEntity<ApiResponse<Void>> verifierEtCreerAlertes() {
        mouvementStockService.verifierEtCreerAlertes();

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .timestamp(LocalDateTime.now())
                .status(200)
                .message("Vérification des alertes terminée")
                .build());
    }
}