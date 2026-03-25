package ma.cremelogic.CremeLogic.ma.controller;

import ma.cremelogic.CremeLogic.ma.dto.response.AlerteResponse;
import ma.cremelogic.CremeLogic.ma.service.interfaces.AlerteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alertes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AlerteController {

    private final AlerteService alerteService;

    @GetMapping("/non-resolues")
    public ResponseEntity<List<AlerteResponse>> getAlertesNonResolues() {
        return ResponseEntity.ok(alerteService.getAlertesNonResolues());
    }

    @PostMapping("/{id}/resoudre")
    public ResponseEntity<AlerteResponse> resoudreAlerte(@PathVariable Long id, @RequestBody(required = false) String commentaire) {
        return ResponseEntity.ok(alerteService.resoudreAlerte(id, commentaire));
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countNonResolues() {
        return ResponseEntity.ok(alerteService.countAlertesNonResolues());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF')")
    public ResponseEntity<Void> supprimerAlerte(@PathVariable Long id) {
        alerteService.supprimerAlerte(id);
        return ResponseEntity.noContent().build();
    }
}
