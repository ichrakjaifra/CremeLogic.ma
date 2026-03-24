package ma.cremelogic.CremeLogic.ma.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.cremelogic.CremeLogic.ma.dto.request.CommandeAchatRequest;
import ma.cremelogic.CremeLogic.ma.dto.request.ReceptionCommandeRequest;
import ma.cremelogic.CremeLogic.ma.dto.response.ApiResponse;
import ma.cremelogic.CremeLogic.ma.dto.response.CommandeAchatResponse;
import ma.cremelogic.CremeLogic.ma.service.interfaces.CommandeAchatService;
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
@RequestMapping("/commandes-achat")
@RequiredArgsConstructor
@Tag(name = "Commandes d'Achat", description = "Gestion des commandes d'achat aux fournisseurs")
@SecurityRequirement(name = "Bearer Authentication")
public class CommandeAchatController {

    private final CommandeAchatService commandeAchatService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MAGASINIER')")
    @Operation(summary = "Créer une nouvelle commande d'achat")
    public ResponseEntity<ApiResponse<CommandeAchatResponse>> createCommande(
            @Valid @RequestBody CommandeAchatRequest request) {

        CommandeAchatResponse response = commandeAchatService.createCommande(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<CommandeAchatResponse>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.CREATED.value())
                        .message("Commande d'achat créée avec succès")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAGASINIER')")
    @Operation(summary = "Mettre à jour une commande d'achat")
    public ResponseEntity<ApiResponse<CommandeAchatResponse>> updateCommande(
            @PathVariable Long id,
            @Valid @RequestBody CommandeAchatRequest request) {

        CommandeAchatResponse response = commandeAchatService.updateCommande(id, request);

        return ResponseEntity.ok(
                ApiResponse.<CommandeAchatResponse>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Commande d'achat mise à jour avec succès")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAGASINIER')")
    @Operation(summary = "Récupérer une commande d'achat par ID")
    public ResponseEntity<ApiResponse<CommandeAchatResponse>> getCommande(@PathVariable Long id) {
        CommandeAchatResponse response = commandeAchatService.getCommande(id);

        return ResponseEntity.ok(
                ApiResponse.<CommandeAchatResponse>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Commande d'achat récupérée avec succès")
                        .data(response)
                        .build()
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MAGASINIER')")
    @Operation(summary = "Lister toutes les commandes d'achat")
    public ResponseEntity<ApiResponse<List<CommandeAchatResponse>>> getAllCommandes() {
        List<CommandeAchatResponse> response = commandeAchatService.getAllCommandes();

        return ResponseEntity.ok(
                ApiResponse.<List<CommandeAchatResponse>>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Commandes d'achat récupérées avec succès")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/fournisseur/{fournisseurId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAGASINIER')")
    @Operation(summary = "Lister les commandes d'un fournisseur")
    public ResponseEntity<ApiResponse<List<CommandeAchatResponse>>> getCommandesByFournisseur(
            @PathVariable Long fournisseurId) {

        List<CommandeAchatResponse> response = commandeAchatService.getCommandesByFournisseur(fournisseurId);

        return ResponseEntity.ok(
                ApiResponse.<List<CommandeAchatResponse>>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Commandes du fournisseur récupérées avec succès")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/statut/{statut}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAGASINIER')")
    @Operation(summary = "Lister les commandes par statut")
    public ResponseEntity<ApiResponse<List<CommandeAchatResponse>>> getCommandesByStatut(
            @PathVariable String statut) {

        List<CommandeAchatResponse> response = commandeAchatService.getCommandesByStatut(statut);

        return ResponseEntity.ok(
                ApiResponse.<List<CommandeAchatResponse>>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Commandes par statut récupérées avec succès")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAGASINIER')")
    @Operation(summary = "Supprimer une commande d'achat")
    public ResponseEntity<ApiResponse<Void>> deleteCommande(@PathVariable Long id) {
        commandeAchatService.deleteCommande(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Commande d'achat supprimée avec succès")
                        .build()
        );
    }

    @PatchMapping("/{id}/statut")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAGASINIER')")
    @Operation(summary = "Changer le statut d'une commande")
    public ResponseEntity<ApiResponse<CommandeAchatResponse>> changerStatut(
            @PathVariable Long id,
            @RequestParam String statut) {

        CommandeAchatResponse response = commandeAchatService.changerStatut(id, statut);

        return ResponseEntity.ok(
                ApiResponse.<CommandeAchatResponse>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Statut modifié avec succès")
                        .data(response)
                        .build()
        );
    }

    @PostMapping("/{id}/recevoir")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAGASINIER')")
    @Operation(summary = "Réceptionner une commande")
    public ResponseEntity<ApiResponse<CommandeAchatResponse>> recevoirCommande(
            @PathVariable Long id,
            @Valid @RequestBody ReceptionCommandeRequest request) {

        CommandeAchatResponse response = commandeAchatService.recevoirCommande(id, request);

        return ResponseEntity.ok(
                ApiResponse.<CommandeAchatResponse>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Commande réceptionnée avec succès")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/en-retard")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAGASINIER')")
    @Operation(summary = "Lister les commandes en retard")
    public ResponseEntity<ApiResponse<List<CommandeAchatResponse>>> getCommandesEnRetard() {
        List<CommandeAchatResponse> response = commandeAchatService.getCommandesEnRetard();

        return ResponseEntity.ok(
                ApiResponse.<List<CommandeAchatResponse>>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Commandes en retard récupérées avec succès")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/montant-periode")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAGASINIER')")
    @Operation(summary = "Montant total des commandes sur une période")
    public ResponseEntity<ApiResponse<BigDecimal>> getMontantTotalCommandesPeriode(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {

        BigDecimal response = commandeAchatService.getMontantTotalCommandesPeriode(debut, fin);

        return ResponseEntity.ok(
                ApiResponse.<BigDecimal>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Montant total des commandes récupéré avec succès")
                        .data(response)
                        .build()
        );
    }

    @PostMapping("/{id}/annuler")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAGASINIER')")
    @Operation(summary = "Annuler une commande")
    public ResponseEntity<ApiResponse<CommandeAchatResponse>> annulerCommande(
            @PathVariable Long id,
            @RequestParam String raison) {

        CommandeAchatResponse response = commandeAchatService.annulerCommande(id, raison);

        return ResponseEntity.ok(
                ApiResponse.<CommandeAchatResponse>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Commande annulée avec succès")
                        .data(response)
                        .build()
        );
    }

    @PostMapping("/{id}/dupliquer")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAGASINIER')")
    @Operation(summary = "Dupliquer une commande")
    public ResponseEntity<ApiResponse<CommandeAchatResponse>> dupliquerCommande(@PathVariable Long id) {
        CommandeAchatResponse response = commandeAchatService.dupliquerCommande(id);

        return ResponseEntity.ok(
                ApiResponse.<CommandeAchatResponse>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Commande dupliquée avec succès")
                        .data(response)
                        .build()
        );
    }
}