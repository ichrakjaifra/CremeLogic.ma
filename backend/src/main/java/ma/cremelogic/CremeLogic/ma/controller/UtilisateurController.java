package ma.cremelogic.CremeLogic.ma.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.cremelogic.CremeLogic.ma.dto.request.CreateUtilisateurRequest;
import ma.cremelogic.CremeLogic.ma.dto.request.UpdateUtilisateurRequest;
import ma.cremelogic.CremeLogic.ma.dto.request.ResetPasswordRequest;
import ma.cremelogic.CremeLogic.ma.dto.response.UtilisateurResponse;
import ma.cremelogic.CremeLogic.ma.service.interfaces.UtilisateurService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/utilisateurs")
@RequiredArgsConstructor
@Tag(name = "Utilisateurs", description = "Gestion des utilisateurs")
@SecurityRequirement(name = "Bearer Authentication")
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    // ============ READ OPERATIONS ============

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Liste tous les utilisateurs")
    public ResponseEntity<List<UtilisateurResponse>> getAllUtilisateurs() {
        return ResponseEntity.ok(utilisateurService.getAllUtilisateurs());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isSelf(#id)")
    @Operation(summary = "Récupère un utilisateur par ID")
    public ResponseEntity<UtilisateurResponse> getUtilisateurById(@PathVariable Long id) {
        return ResponseEntity.ok(utilisateurService.getUtilisateurById(id));
    }

    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Récupère un utilisateur par email")
    public ResponseEntity<UtilisateurResponse> getUtilisateurByEmail(@PathVariable String email) {
        return ResponseEntity.ok(utilisateurService.getUtilisateurByEmail(email));
    }

    @GetMapping("/me")
    @Operation(summary = "Récupère les informations de l'utilisateur connecté")
    public ResponseEntity<UtilisateurResponse> getCurrentUser() {
        return ResponseEntity.ok(utilisateurService.getCurrentUser());
    }

    // ============ CREATE OPERATION ============

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crée un nouvel utilisateur")
    public ResponseEntity<UtilisateurResponse> createUtilisateur(@Valid @RequestBody CreateUtilisateurRequest request) {
        return new ResponseEntity<>(utilisateurService.createUtilisateur(request), HttpStatus.CREATED);
    }

    // ============ UPDATE OPERATIONS ============

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Met à jour un utilisateur")
    public ResponseEntity<UtilisateurResponse> updateUtilisateur(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUtilisateurRequest request) {
        return ResponseEntity.ok(utilisateurService.updateUtilisateur(id, request));
    }

    @PatchMapping("/{id}/toggle-actif")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Active/désactive un utilisateur")
    public ResponseEntity<Void> toggleActif(@PathVariable Long id) {
        utilisateurService.toggleActif(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/reset-password")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Réinitialise le mot de passe d'un utilisateur")
    public ResponseEntity<Void> resetPassword(
            @PathVariable Long id,
            @Valid @RequestBody ResetPasswordRequest request) {
        utilisateurService.resetPassword(id, request);
        return ResponseEntity.ok().build();
    }

    // ============ DELETE OPERATION ============

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Supprime un utilisateur")
    public ResponseEntity<Void> deleteUtilisateur(@PathVariable Long id) {
        utilisateurService.deleteUtilisateur(id);
        return ResponseEntity.noContent().build();
    }

    // ============ STATISTICS ============

    @GetMapping("/stats/total")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Nombre total d'utilisateurs")
    public ResponseEntity<Long> getTotalUtilisateurs() {
        return ResponseEntity.ok(utilisateurService.getTotalUtilisateurs());
    }

    @GetMapping("/stats/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Nombre d'utilisateurs par rôle")
    public ResponseEntity<Long> getCountByRole(@PathVariable String role) {
        return ResponseEntity.ok(utilisateurService.getCountByRole(role));
    }
}