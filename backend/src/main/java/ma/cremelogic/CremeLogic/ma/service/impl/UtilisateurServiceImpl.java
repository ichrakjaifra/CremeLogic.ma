package ma.cremelogic.CremeLogic.ma.service.impl;

import lombok.RequiredArgsConstructor;
import ma.cremelogic.CremeLogic.ma.dto.request.CreateUtilisateurRequest;
import ma.cremelogic.CremeLogic.ma.dto.request.UpdateUtilisateurRequest;
import ma.cremelogic.CremeLogic.ma.dto.request.ResetPasswordRequest;
import ma.cremelogic.CremeLogic.ma.dto.response.UtilisateurResponse;
import ma.cremelogic.CremeLogic.ma.entity.Utilisateur;
import ma.cremelogic.CremeLogic.ma.enums.Role;
import ma.cremelogic.CremeLogic.ma.exception.ResourceNotFoundException;
import ma.cremelogic.CremeLogic.ma.exception.UnauthorizedException;
import ma.cremelogic.CremeLogic.ma.repository.UtilisateurRepository;
import ma.cremelogic.CremeLogic.ma.service.interfaces.UtilisateurService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UtilisateurServiceImpl implements UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UtilisateurResponse> getAllUtilisateurs() {
        return utilisateurRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public UtilisateurResponse getUtilisateurById(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.forUtilisateur(id));
        return mapToResponse(utilisateur);
    }

    @Override
    public UtilisateurResponse getUtilisateurByEmail(String email) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> ResourceNotFoundException.forUtilisateurByEmail(email));
        return mapToResponse(utilisateur);
    }

    @Override
    @Transactional
    public UtilisateurResponse createUtilisateur(CreateUtilisateurRequest request) {
        // Vérifier si l'email existe déjà
        if (utilisateurRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Un utilisateur avec cet email existe déjà");
        }

        Utilisateur utilisateur = Utilisateur.builder()
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .email(request.getEmail())
                .motDePasse(passwordEncoder.encode(request.getMotDePasse()))
                .telephone(request.getTelephone())
                .role(request.getRole())
                .actif(true)
                .build();

        Utilisateur saved = utilisateurRepository.save(utilisateur);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public UtilisateurResponse updateUtilisateur(Long id, UpdateUtilisateurRequest request) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.forUtilisateur(id));

        // Vérifier si l'email est déjà utilisé par un autre utilisateur
        if (!utilisateur.getEmail().equals(request.getEmail()) &&
                utilisateurRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Cet email est déjà utilisé");
        }

        utilisateur.setNom(request.getNom());
        utilisateur.setPrenom(request.getPrenom());
        utilisateur.setEmail(request.getEmail());
        utilisateur.setTelephone(request.getTelephone());

        if (request.getRole() != null) {
            utilisateur.setRole(request.getRole());
        }

        if (request.getActif() != null) {
            utilisateur.setActif(request.getActif());
        }

        Utilisateur saved = utilisateurRepository.save(utilisateur);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public void deleteUtilisateur(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.forUtilisateur(id));

        // Empêcher la suppression de son propre compte
        var auth = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = (auth != null) ? auth.getName() : null;
        if (utilisateur.getEmail().equals(currentEmail)) {
            throw new UnauthorizedException("Vous ne pouvez pas supprimer votre propre compte");
        }

        utilisateurRepository.delete(utilisateur);
    }

    @Override
    @Transactional
    public UtilisateurResponse toggleActif(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.forUtilisateur(id));

        // Empêcher la désactivation de son propre compte
        var auth = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = (auth != null) ? auth.getName() : null;
        if (utilisateur.getEmail().equals(currentEmail)) {
            throw new UnauthorizedException("Vous ne pouvez pas désactiver votre propre compte");
        }

        utilisateur.setActif(!utilisateur.isActif());
        return mapToResponse(utilisateurRepository.save(utilisateur));
    }

    @Override
    @Transactional
    public void resetPassword(Long id, ResetPasswordRequest request) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.forUtilisateur(id));

        utilisateur.setMotDePasse(passwordEncoder.encode(request.getNewPassword()));
        utilisateurRepository.save(utilisateur);
    }

    @Override
    public UtilisateurResponse getCurrentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new UnauthorizedException("Session expirée ou invalide", "SESSION_EXPIRED");
        }
        String email = auth.getName();
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "email", email));
        return mapToResponse(utilisateur);
    }

    @Override
    public long getTotalUtilisateurs() {
        return utilisateurRepository.count();
    }

    @Override
    public long getCountByRole(String role) {
        try {
            Role roleEnum = Role.valueOf(role.toUpperCase());
            return utilisateurRepository.findByRole(roleEnum).size();
        } catch (IllegalArgumentException e) {
            return 0;
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
}