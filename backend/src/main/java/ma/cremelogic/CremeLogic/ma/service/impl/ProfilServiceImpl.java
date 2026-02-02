package ma.cremelogic.CremeLogic.ma.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import ma.cremelogic.CremeLogic.ma.dto.request.ChangePasswordRequest;
import ma.cremelogic.CremeLogic.ma.dto.request.UpdateProfileRequest;
import ma.cremelogic.CremeLogic.ma.dto.response.HistoriqueActiviteResponse;
import ma.cremelogic.CremeLogic.ma.dto.response.UtilisateurResponse;
import ma.cremelogic.CremeLogic.ma.entity.HistoriqueActivite;
import ma.cremelogic.CremeLogic.ma.entity.Utilisateur;
import ma.cremelogic.CremeLogic.ma.exception.ResourceNotFoundException;
import ma.cremelogic.CremeLogic.ma.exception.UnauthorizedException;
import ma.cremelogic.CremeLogic.ma.repository.HistoriqueActiviteRepository;
import ma.cremelogic.CremeLogic.ma.repository.UtilisateurRepository;
import ma.cremelogic.CremeLogic.ma.service.interfaces.ProfilService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfilServiceImpl implements ProfilService {

    private final UtilisateurRepository utilisateurRepository;
    private final HistoriqueActiviteRepository historiqueActiviteRepository;
    private final PasswordEncoder passwordEncoder;
    private final HttpServletRequest httpServletRequest;

    @Override
    @Transactional
    public UtilisateurResponse updateProfile(UpdateProfileRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "email", email));

        // Vérifier si l'email est déjà utilisé par un autre utilisateur
        if (!utilisateur.getEmail().equals(request.getEmail()) &&
                utilisateurRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Cet email est déjà utilisé");
        }

        utilisateur.setNom(request.getNom());
        utilisateur.setPrenom(request.getPrenom());
        utilisateur.setEmail(request.getEmail());
        utilisateur.setTelephone(request.getTelephone());

        Utilisateur saved = utilisateurRepository.save(utilisateur);

        // Log de l'activité
        logActivite("UPDATE_PROFILE",
                "Mise à jour du profil utilisateur",
                getClientIp(),
                httpServletRequest.getHeader("User-Agent"));

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "email", email));

        // Vérifier l'ancien mot de passe
        if (!passwordEncoder.matches(request.getOldPassword(), utilisateur.getMotDePasse())) {
            throw new UnauthorizedException("L'ancien mot de passe est incorrect", "INVALID_PASSWORD");
        }

        // Vérifier que le nouveau mot de passe est différent
        if (passwordEncoder.matches(request.getNewPassword(), utilisateur.getMotDePasse())) {
            throw new IllegalArgumentException("Le nouveau mot de passe doit être différent de l'ancien");
        }

        // Vérifier la confirmation du mot de passe
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Les mots de passe ne correspondent pas");
        }

        // Mettre à jour le mot de passe
        utilisateur.setMotDePasse(passwordEncoder.encode(request.getNewPassword()));
        utilisateurRepository.save(utilisateur);

        // Log de l'activité
        logActivite("CHANGE_PASSWORD",
                "Changement du mot de passe",
                getClientIp(),
                httpServletRequest.getHeader("User-Agent"));
    }

    @Override
    public Page<HistoriqueActiviteResponse> getHistoriqueActivite(Pageable pageable) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "email", email));

        return historiqueActiviteRepository
                .findByUtilisateurIdOrderByDateActionDesc(utilisateur.getId(), pageable)
                .map(this::mapToHistoriqueResponse);
    }

    @Override
    @Transactional
    public void logActivite(String action, String description, String ipAddress, String userAgent) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email).orElse(null);

        if (utilisateur != null) {
            HistoriqueActivite historique = HistoriqueActivite.builder()
                    .utilisateur(utilisateur)
                    .action(action)
                    .description(description)
                    .ipAddress(ipAddress)
                    .userAgent(userAgent)
                    .build();

            historiqueActiviteRepository.save(historique);
        }
    }

    private UtilisateurResponse mapToResponse(Utilisateur utilisateur) {
        return UtilisateurResponse.builder()
                .id(utilisateur.getId())
                .nom(utilisateur.getNom())
                .prenom(utilisateur.getPrenom())
                .email(utilisateur.getEmail())
                .telephone(utilisateur.getTelephone())
                .role(utilisateur.getRole())
                .actif(utilisateur.isActif())
                .dateCreation(utilisateur.getDateCreation())
                .dateModification(utilisateur.getDateModification())
                .build();
    }

    private HistoriqueActiviteResponse mapToHistoriqueResponse(HistoriqueActivite historique) {
        return HistoriqueActiviteResponse.builder()
                .id(historique.getId())
                .action(historique.getAction())
                .description(historique.getDescription())
                .ipAddress(historique.getIpAddress())
                .dateAction(historique.getDateAction())
                .build();
    }

    private String getClientIp() {
        String xfHeader = httpServletRequest.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return httpServletRequest.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}