package ma.cremelogic.CremeLogic.ma.controller;

import ma.cremelogic.CremeLogic.ma.config.JwtService;
import ma.cremelogic.CremeLogic.ma.LoginRequest;
import ma.cremelogic.CremeLogic.ma.dto.response.AuthResponse;
import ma.cremelogic.CremeLogic.ma.entity.Utilisateur;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        Utilisateur utilisateur = (Utilisateur) authentication.getPrincipal();
        String jwtToken = jwtService.generateToken(utilisateur);

        AuthResponse response = AuthResponse.builder()
                .token(jwtToken)
                .id(utilisateur.getId())
                .nom(utilisateur.getNom())
                .prenom(utilisateur.getPrenom())
                .email(utilisateur.getEmail())
                .role(utilisateur.getRole())
                .telephone(utilisateur.getTelephone())
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        // In a stateless JWT implementation, logout is handled client-side
        return ResponseEntity.ok().build();
    }
}
