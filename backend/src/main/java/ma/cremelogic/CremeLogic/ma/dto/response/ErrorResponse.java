package ma.cremelogic.CremeLogic.ma.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private Map<String, String> details;

    @Builder.Default
    private String errorCode = "GENERAL_ERROR";

    // Conserver uniquement les méthodes factory
    public static ErrorResponse validationError(Map<String, String> details) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(400)
                .error("Validation Error")
                .message("Erreurs de validation détectées")
                .errorCode("VALIDATION_ERROR")
                .details(details)
                .build();
    }

    public static ErrorResponse authenticationError(String message) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(401)
                .error("Authentication Error")
                .message(message)
                .errorCode("AUTHENTICATION_ERROR")
                .build();
    }

    public static ErrorResponse authorizationError(String message) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(403)
                .error("Authorization Error")
                .message(message)
                .errorCode("AUTHORIZATION_ERROR")
                .build();
    }

    public static ErrorResponse notFoundError(String resourceName, String fieldName, Object fieldValue) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(404)
                .error("Resource Not Found")
                .message(String.format("%s non trouvé avec %s : '%s'", resourceName, fieldName, fieldValue))
                .errorCode("RESOURCE_NOT_FOUND")
                .build();
    }
}
