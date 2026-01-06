package ma.cremelogic.CremeLogic.ma.service.interfaces;

import ma.cremelogic.CremeLogic.ma.dto.response.UtilisateurResponse;

import java.util.List;

public interface UtilisateurService {
    List<UtilisateurResponse> getAllUtilisateurs();
    UtilisateurResponse getUtilisateurById(Long id);
    UtilisateurResponse getCurrentUser();
    void deleteUtilisateur(Long id);
    void toggleActif(Long id);
}
