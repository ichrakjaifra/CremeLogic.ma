package ma.cremelogic.CremeLogic.ma.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import ma.cremelogic.CremeLogic.ma.dto.response.ApiResponse;
import ma.cremelogic.CremeLogic.ma.dto.response.DashboardResponse;
import ma.cremelogic.CremeLogic.ma.service.interfaces.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Tableaux de bord par rôle")
@SecurityRequirement(name = "Bearer Authentication")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Dashboard Admin - Vue complète")
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboardAdmin() {
        DashboardResponse response = dashboardService.getDashboardAdmin();

        return ResponseEntity.ok(
                ApiResponse.<DashboardResponse>builder()
                        .timestamp(LocalDateTime.now())
                        .status(200)
                        .message("Dashboard Admin récupéré avec succès")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/chef")
    @PreAuthorize("hasRole('CHEF')")
    @Operation(summary = "Dashboard Chef - Vue production")
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboardChef() {
        DashboardResponse response = dashboardService.getDashboardChef();

        return ResponseEntity.ok(
                ApiResponse.<DashboardResponse>builder()
                        .timestamp(LocalDateTime.now())
                        .status(200)
                        .message("Dashboard Chef récupéré avec succès")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/magasinier")
    @PreAuthorize("hasRole('MAGASINIER')")
    @Operation(summary = "Dashboard Magasinier - Vue stock")
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboardMagasinier() {
        DashboardResponse response = dashboardService.getDashboardMagasinier();

        return ResponseEntity.ok(
                ApiResponse.<DashboardResponse>builder()
                        .timestamp(LocalDateTime.now())
                        .status(200)
                        .message("Dashboard Magasinier récupéré avec succès")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/employe")
    @PreAuthorize("hasRole('EMPLOYE')")
    @Operation(summary = "Dashboard Employé - Vue personnelle")
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboardEmploye() {
        DashboardResponse response = dashboardService.getDashboardEmploye();

        return ResponseEntity.ok(
                ApiResponse.<DashboardResponse>builder()
                        .timestamp(LocalDateTime.now())
                        .status(200)
                        .message("Dashboard Employé récupéré avec succès")
                        .data(response)
                        .build()
        );
    }
}