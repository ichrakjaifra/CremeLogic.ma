package ma.cremelogic.CremeLogic.ma.service.interfaces;

import ma.cremelogic.CremeLogic.ma.dto.request.ChangePasswordRequest;
import ma.cremelogic.CremeLogic.ma.dto.request.UpdateProfileRequest;
import ma.cremelogic.CremeLogic.ma.dto.response.HistoriqueActiviteResponse;
import ma.cremelogic.CremeLogic.ma.dto.response.UtilisateurResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProfilService {
    UtilisateurResponse updateProfile(UpdateProfileRequest request);
    void changePassword(ChangePasswordRequest request);
    Page<HistoriqueActiviteResponse> getHistoriqueActivite(Pageable pageable);
    void logActivite(String action, String description, String ipAddress, String userAgent);
}