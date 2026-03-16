package ma.cremelogic.CremeLogic.ma.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.cremelogic.CremeLogic.ma.dto.request.VenteRequest;
import ma.cremelogic.CremeLogic.ma.dto.response.ApiResponse;
import ma.cremelogic.CremeLogic.ma.dto.response.ProduitResponse;
import ma.cremelogic.CremeLogic.ma.dto.response.VenteResponse;
import ma.cremelogic.CremeLogic.ma.service.interfaces.VenteService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ventes")
@RequiredArgsConstructor
@Tag(name = "Ventes", description = "Gestion des ventes")
@SecurityRequirement(name = "Bearer Authentication")
public class VenteController {

    private final VenteService venteService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYE')")
    @Operation(summary = "Enregistrer une nouvelle vente")
    public ResponseEntity<ApiResponse<VenteResponse>> createVente(@Valid @RequestBody VenteRequest request) {
        VenteResponse response = venteService.createVente(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<VenteResponse>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.CREATED.value())
                        .message("Vente enregistrée avec succès")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYE')")
    @Operation(summary = "Récupérer une vente par ID")
    public ResponseEntity<ApiResponse<VenteResponse>> getVente(@PathVariable Long id) {
        VenteResponse response = venteService.getVente(id);

        return ResponseEntity.ok(
                ApiResponse.<VenteResponse>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Vente récupérée avec succès")
                        .data(response)
                        .build()
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYE')")
    @Operation(summary = "Lister toutes les ventes")
    public ResponseEntity<ApiResponse<List<VenteResponse>>> getAllVentes() {
        List<VenteResponse> response = venteService.getAllVentes();

        return ResponseEntity.ok(
                ApiResponse.<List<VenteResponse>>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Ventes récupérées avec succès")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/periode")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYE')")
    @Operation(summary = "Lister les ventes par période")
    public ResponseEntity<ApiResponse<List<VenteResponse>>> getVentesByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {

        List<VenteResponse> response = venteService.getVentesByDate(debut, fin);

        return ResponseEntity.ok(
                ApiResponse.<List<VenteResponse>>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Ventes par période récupérées avec succès")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/caissier/{caissierId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYE')")
    @Operation(summary = "Lister les ventes par caissier")
    public ResponseEntity<ApiResponse<List<VenteResponse>>> getVentesByCaissier(@PathVariable Long caissierId) {
        List<VenteResponse> response = venteService.getVentesByCaissier(caissierId);

        return ResponseEntity.ok(
                ApiResponse.<List<VenteResponse>>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Ventes par caissier récupérées avec succès")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/client")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYE')")
    @Operation(summary = "Rechercher des ventes par client")
    public ResponseEntity<ApiResponse<List<VenteResponse>>> searchVentesByClient(@RequestParam String recherche) {
        List<VenteResponse> response = venteService.searchVentesByClient(recherche);

        return ResponseEntity.ok(
                ApiResponse.<List<VenteResponse>>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Ventes recherchées avec succès")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Annuler une vente")
    public ResponseEntity<ApiResponse<Void>> annulerVente(@PathVariable Long id, @RequestParam String raison) {
        venteService.annulerVente(id, raison);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Vente annulée avec succès")
                        .build()
        );
    }

    @GetMapping("/{id}/facture")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYE')")
    @Operation(summary = "Générer une facture pour une vente")
    public ResponseEntity<ApiResponse<VenteResponse>> genererFacture(@PathVariable Long id) {
        VenteResponse response = venteService.genererFacture(id);

        return ResponseEntity.ok(
                ApiResponse.<VenteResponse>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Facture générée avec succès")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/chiffre-affaires")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYE')")
    @Operation(summary = "Chiffre d'affaires sur une période")
    public ResponseEntity<ApiResponse<BigDecimal>> getChiffreAffairesPeriode(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {

        BigDecimal response = venteService.getChiffreAffairesPeriode(debut, fin);

        return ResponseEntity.ok(
                ApiResponse.<BigDecimal>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Chiffre d'affaires récupéré avec succès")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/nombre-ventes")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYE')")
    @Operation(summary = "Nombre de ventes sur une période")
    public ResponseEntity<ApiResponse<Long>> getNombreVentesPeriode(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {

        Long response = venteService.getNombreVentesPeriode(debut, fin);

        return ResponseEntity.ok(
                ApiResponse.<Long>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Nombre de ventes récupéré avec succès")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/recentes")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYE')")
    @Operation(summary = "Récupérer les ventes récentes")
    public ResponseEntity<ApiResponse<List<VenteResponse>>> getVentesRecent(@RequestParam(defaultValue = "10") int limit) {
        List<VenteResponse> response = venteService.getVentesRecent(limit);

        return ResponseEntity.ok(
                ApiResponse.<List<VenteResponse>>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Ventes récentes récupérées avec succès")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/par-categorie")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYE')")
    @Operation(summary = "Ventes par catégorie sur une période")
    public ResponseEntity<ApiResponse<Map<String, BigDecimal>>> getVentesParCategorie(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {

        Map<String, BigDecimal> response = venteService.getVentesParCategorie(debut, fin);

        return ResponseEntity.ok(
                ApiResponse.<Map<String, BigDecimal>>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Ventes par catégorie récupérées avec succès")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/produits-plus-vendus")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYE')")
    @Operation(summary = "Produits les plus vendus sur une période")
    public ResponseEntity<ApiResponse<List<ProduitResponse>>> getProduitsPlusVendus(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin,
            @RequestParam(defaultValue = "10") int limit) {

        List<ProduitResponse> response = venteService.getProduitsPlusVendus(debut, fin, limit);

        return ResponseEntity.ok(
                ApiResponse.<List<ProduitResponse>>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Produits les plus vendus récupérés avec succès")
                        .data(response)
                        .build()
        );
    }
}