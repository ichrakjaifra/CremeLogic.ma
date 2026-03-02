package ma.cremelogic.CremeLogic.ma.controller;

import ma.cremelogic.CremeLogic.ma.dto.request.FournisseurRequest;
import ma.cremelogic.CremeLogic.ma.dto.response.ApiResponse;
import ma.cremelogic.CremeLogic.ma.dto.response.FournisseurResponse;
import ma.cremelogic.CremeLogic.ma.service.interfaces.FournisseurService;
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
@RequestMapping("/api/fournisseurs")
@RequiredArgsConstructor
@Tag(name = "Fournisseurs", description = "Gestion des fournisseurs")
public class FournisseurController {

    private final FournisseurService fournisseurService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MAGASINIER')")
    @Operation(summary = "Créer un nouveau fournisseur")
    public ResponseEntity<ApiResponse<FournisseurResponse>> createFournisseur(@Valid @RequestBody FournisseurRequest request) {
        FournisseurResponse response = fournisseurService.createFournisseur(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<FournisseurResponse>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.CREATED.value())
                        .message("Fournisseur créé avec succès")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MAGASINIER')")
    @Operation(summary = "Mettre à jour un fournisseur")
    public ResponseEntity<ApiResponse<FournisseurResponse>> updateFournisseur(
            @PathVariable Long id,
            @Valid @RequestBody FournisseurRequest request) {
        FournisseurResponse response = fournisseurService.updateFournisseur(id, request);
        return ResponseEntity.ok(
                ApiResponse.<FournisseurResponse>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Fournisseur mis à jour avec succès")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF', 'MAGASINIER')")
    @Operation(summary = "Récupérer un fournisseur par ID")
    public ResponseEntity<ApiResponse<FournisseurResponse>> getFournisseur(@PathVariable Long id) {
        FournisseurResponse response = fournisseurService.getFournisseur(id);
        return ResponseEntity.ok(
                ApiResponse.<FournisseurResponse>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Fournisseur récupéré avec succès")
                        .data(response)
                        .build()
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF', 'MAGASINIER')")
    @Operation(summary = "Lister tous les fournisseurs")
    public ResponseEntity<ApiResponse<List<FournisseurResponse>>> getAllFournisseurs() {
        List<FournisseurResponse> response = fournisseurService.getAllFournisseurs();
        return ResponseEntity.ok(
                ApiResponse.<List<FournisseurResponse>>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Fournisseurs récupérés avec succès")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/actifs")
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF', 'MAGASINIER')")
    @Operation(summary = "Lister les fournisseurs actifs")
    public ResponseEntity<ApiResponse<List<FournisseurResponse>>> getFournisseursActifs() {
        List<FournisseurResponse> response = fournisseurService.getFournisseursActifs();
        return ResponseEntity.ok(
                ApiResponse.<List<FournisseurResponse>>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Fournisseurs actifs récupérés avec succès")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF', 'MAGASINIER')")
    @Operation(summary = "Rechercher des fournisseurs")
    public ResponseEntity<ApiResponse<List<FournisseurResponse>>> searchFournisseurs(@RequestParam String keyword) {
        List<FournisseurResponse> response = fournisseurService.searchFournisseurs(keyword);
        return ResponseEntity.ok(
                ApiResponse.<List<FournisseurResponse>>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Fournisseurs recherchés avec succès")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Supprimer un fournisseur")
    public ResponseEntity<ApiResponse<Void>> deleteFournisseur(@PathVariable Long id) {
        fournisseurService.deleteFournisseur(id);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Fournisseur supprimé avec succès")
                        .build()
        );
    }

    @PostMapping("/{id}/evaluer")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAGASINIER')")
    @Operation(summary = "Évaluer un fournisseur")
    public ResponseEntity<ApiResponse<FournisseurResponse>> evaluerFournisseur(
            @PathVariable Long id,
            @RequestParam Double note,
            @RequestParam(required = false) String commentaire) {
        FournisseurResponse response = fournisseurService.evaluerFournisseur(id, note, commentaire);
        return ResponseEntity.ok(
                ApiResponse.<FournisseurResponse>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Fournisseur évalué avec succès")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/ville/{ville}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAGASINIER')")
    @Operation(summary = "Lister les fournisseurs par ville")
    public ResponseEntity<ApiResponse<List<FournisseurResponse>>> getFournisseursParVille(@PathVariable String ville) {
        List<FournisseurResponse> response = fournisseurService.getFournisseursParVille(ville);
        return ResponseEntity.ok(
                ApiResponse.<List<FournisseurResponse>>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Fournisseurs par ville récupérés avec succès")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{id}/montant-commandes")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAGASINIER')")
    @Operation(summary = "Récupérer le montant total des commandes d'un fournisseur")
    public ResponseEntity<ApiResponse<Map<String, BigDecimal>>> getMontantTotalCommandes(@PathVariable Long id) {
        BigDecimal montant = fournisseurService.getMontantTotalCommandes(id);
        return ResponseEntity.ok(
                ApiResponse.<Map<String, BigDecimal>>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Montant total des commandes récupéré avec succès")
                        .data(Map.of("montantTotalCommandes", montant))
                        .build()
        );
    }

    @GetMapping("/{id}/nombre-commandes")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAGASINIER')")
    @Operation(summary = "Récupérer le nombre de commandes d'un fournisseur")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> getNombreCommandes(@PathVariable Long id) {
        Integer nombre = fournisseurService.getNombreCommandes(id);
        return ResponseEntity.ok(
                ApiResponse.<Map<String, Integer>>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Nombre de commandes récupéré avec succès")
                        .data(Map.of("nombreCommandes", nombre))
                        .build()
        );
    }

    @PostMapping("/{id}/toggle-actif")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activer/désactiver un fournisseur")
    public ResponseEntity<ApiResponse<FournisseurResponse>> toggleActif(@PathVariable Long id) {
        FournisseurResponse response = fournisseurService.toggleActif(id);
        return ResponseEntity.ok(
                ApiResponse.<FournisseurResponse>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Statut du fournisseur modifié avec succès")
                        .data(response)
                        .build()
        );
    }
}
