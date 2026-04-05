package ma.cremelogic.CremeLogic.ma.service.impl;

import ma.cremelogic.CremeLogic.ma.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service("securityService")
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class SecurityService {

    private final UtilisateurRepository utilisateurRepository;

    @Autowired
    public SecurityService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    public boolean isSelf(Long userId) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }
        String currentUserEmail = auth.getName();
        return utilisateurRepository.findByEmail(currentUserEmail)
                .map(user -> user.getId().equals(userId))
                .orElse(false);
    }
}