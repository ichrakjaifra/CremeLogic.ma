package ma.cremelogic.CremeLogic.ma.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.cremelogic.CremeLogic.ma.dto.request.ChangePasswordRequest;
import ma.cremelogic.CremeLogic.ma.dto.request.UpdateProfileRequest;
import ma.cremelogic.CremeLogic.ma.dto.response.HistoriqueActiviteResponse;
import ma.cremelogic.CremeLogic.ma.dto.response.UtilisateurResponse;
import ma.cremelogic.CremeLogic.ma.service.interfaces.ProfilService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profil")
@RequiredArgsConstructor
@Tag(name = "Profil", description = "Gestion du profil utilisateur")
@SecurityRequirement(name = "Bearer Authentication")
public class ProfilController {

    private final ProfilService profilService;

    @PutMapping("/update")
    @Operation(summary = "Mettre à jour le profil")
    public ResponseEntity<UtilisateurResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(profilService.updateProfile(request));
    }

    @PostMapping("/change-password")
    @Operation(summary = "Changer le mot de passe")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        profilService.changePassword(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/historique")
    @Operation(summary = "Historique des activités")
    public ResponseEntity<Page<HistoriqueActiviteResponse>> getHistoriqueActivite(
            @Parameter(description = "Paramètres de pagination")
            @PageableDefault(size = 10, sort = "dateAction", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(profilService.getHistoriqueActivite(pageable));
    }
}