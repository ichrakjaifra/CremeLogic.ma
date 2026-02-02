package ma.cremelogic.CremeLogic.ma.controller;

import ma.cremelogic.CremeLogic.ma.dto.response.UtilisateurResponse;
import ma.cremelogic.CremeLogic.ma.service.interfaces.UtilisateurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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

    @GetMapping("/me")
    @Operation(summary = "Récupère les informations de l'utilisateur connecté")
    public ResponseEntity<UtilisateurResponse> getCurrentUser() {
        return ResponseEntity.ok(utilisateurService.getCurrentUser());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Supprime un utilisateur")
    public ResponseEntity<Void> deleteUtilisateur(@PathVariable Long id) {
        utilisateurService.deleteUtilisateur(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle-actif")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Active/désactive un utilisateur")
    public ResponseEntity<Void> toggleActif(@PathVariable Long id) {
        utilisateurService.toggleActif(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Récupère un utilisateur par email")
    public ResponseEntity<UtilisateurResponse> getUtilisateurByEmail(@PathVariable String email) {
        return ResponseEntity.ok().build();
    }
}
