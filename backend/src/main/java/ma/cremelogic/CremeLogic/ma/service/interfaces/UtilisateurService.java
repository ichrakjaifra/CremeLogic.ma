package ma.cremelogic.CremeLogic.ma.service.interfaces;

import ma.cremelogic.CremeLogic.ma.dto.request.CreateUtilisateurRequest;
import ma.cremelogic.CremeLogic.ma.dto.request.UpdateUtilisateurRequest;
import ma.cremelogic.CremeLogic.ma.dto.request.ResetPasswordRequest;
import ma.cremelogic.CremeLogic.ma.dto.response.UtilisateurResponse;

import java.util.List;

public interface UtilisateurService {
    // CRUD operations
    List<UtilisateurResponse> getAllUtilisateurs();
    UtilisateurResponse getUtilisateurById(Long id);
    UtilisateurResponse getUtilisateurByEmail(String email);
    UtilisateurResponse createUtilisateur(CreateUtilisateurRequest request);
    UtilisateurResponse updateUtilisateur(Long id, UpdateUtilisateurRequest request);
    void deleteUtilisateur(Long id);

    // Status management
    void toggleActif(Long id);
    void resetPassword(Long id, ResetPasswordRequest request);

    // Current user
    UtilisateurResponse getCurrentUser();

    // Statistics
    long getTotalUtilisateurs();
    long getCountByRole(String role);
}