package ma.cremelogic.CremeLogic.ma.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.math.BigDecimal;

/**
 * Exception levée lors d'une erreur de validation métier
 * Statut HTTP : 400 BAD REQUEST
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ValidationException extends RuntimeException {

    private final String errorCode;

    public ValidationException(String message) {
        super(message);
        this.errorCode = "VALIDATION_ERROR";
    }

    public ValidationException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "VALIDATION_ERROR";
    }

    public ValidationException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    // Factory methods for common validation scenarios
    public static ValidationException invalidField(String field, String reason) {
        return new ValidationException(
                String.format("Champ invalide '%s': %s", field, reason),
                "INVALID_FIELD"
        );
    }

    public static ValidationException missingField(String field) {
        return new ValidationException(
                String.format("Champ obligatoire manquant: %s", field),
                "MISSING_FIELD"
        );
    }

    public static ValidationException duplicateValue(String field, Object value) {
        return new ValidationException(
                String.format("La valeur '%s' pour le champ '%s' existe déjà", value, field),
                "DUPLICATE_VALUE"
        );
    }

    public static ValidationException insufficientStock(String product, BigDecimal available, BigDecimal requested) {
        return new ValidationException(
                String.format("Stock insuffisant pour %s. Disponible: %s, Demandé: %s",
                        product, available, requested),
                "INSUFFICIENT_STOCK"
        );
    }

    public static ValidationException invalidQuantity(String message) {
        return new ValidationException(message, "INVALID_QUANTITY");
    }

    public static ValidationException invalidDate(String message) {
        return new ValidationException(message, "INVALID_DATE");
    }

    public static ValidationException invalidState(String message) {
        return new ValidationException(message, "INVALID_STATE");
    }

    public static ValidationException businessRuleViolation(String message) {
        return new ValidationException(message, "BUSINESS_RULE_VIOLATION");
    }
}