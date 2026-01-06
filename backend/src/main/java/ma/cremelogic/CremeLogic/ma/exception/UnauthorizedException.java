package ma.cremelogic.CremeLogic.ma.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception levée lorsque l'authentification échoue
 * Statut HTTP : 401 UNAUTHORIZED
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnauthorizedException extends RuntimeException {

    private final String errorCode;

    public UnauthorizedException(String message) {
        super(message);
        this.errorCode = "UNAUTHORIZED";
    }

    public UnauthorizedException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "UNAUTHORIZED";
    }

    public UnauthorizedException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    // Factory methods for common unauthorized scenarios
    public static UnauthorizedException invalidCredentials() {
        return new UnauthorizedException(
                "Email ou mot de passe incorrect",
                "INVALID_CREDENTIALS"
        );
    }

    public static UnauthorizedException invalidToken() {
        return new UnauthorizedException(
                "Token JWT invalide ou expiré",
                "INVALID_TOKEN"
        );
    }

    public static UnauthorizedException tokenRequired() {
        return new UnauthorizedException(
                "Token d'authentification requis",
                "TOKEN_REQUIRED"
        );
    }

    public static UnauthorizedException accountDisabled() {
        return new UnauthorizedException(
                "Compte utilisateur désactivé",
                "ACCOUNT_DISABLED"
        );
    }

    public static UnauthorizedException accountLocked() {
        return new UnauthorizedException(
                "Compte utilisateur bloqué",
                "ACCOUNT_LOCKED"
        );
    }
}
