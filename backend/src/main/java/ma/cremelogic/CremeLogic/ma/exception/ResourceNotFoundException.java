package ma.cremelogic.CremeLogic.ma.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception levée lorsqu'une ressource n'est pas trouvée
 * Statut HTTP : 404 NOT FOUND
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    private final String resourceName;
    private final String fieldName;
    private final Object fieldValue;
    private final String errorCode;

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s non trouvé avec %s : '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.errorCode = "RESOURCE_NOT_FOUND";
    }

    public ResourceNotFoundException(String message) {
        super(message);
        this.resourceName = null;
        this.fieldName = null;
        this.fieldValue = null;
        this.errorCode = "RESOURCE_NOT_FOUND";
    }

    public ResourceNotFoundException(String message, String errorCode) {
        super(message);
        this.resourceName = null;
        this.fieldName = null;
        this.fieldValue = null;
        this.errorCode = errorCode;
    }

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue, String errorCode) {
        super(String.format("%s non trouvé avec %s : '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.errorCode = errorCode;
    }

    // Getters
    public String getResourceName() {
        return resourceName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getFieldValue() {
        return fieldValue;
    }

    public String getErrorCode() {
        return errorCode;
    }

    // Factory methods for common resource not found scenarios
    public static ResourceNotFoundException forUtilisateur(Long id) {
        return new ResourceNotFoundException("Utilisateur", "id", id, "USER_NOT_FOUND");
    }

    public static ResourceNotFoundException forUtilisateurByEmail(String email) {
        return new ResourceNotFoundException("Utilisateur", "email", email, "USER_NOT_FOUND");
    }

    public static ResourceNotFoundException forProduit(Long id) {
        return new ResourceNotFoundException("Produit", "id", id, "PRODUCT_NOT_FOUND");
    }

    public static ResourceNotFoundException forProduitByCode(String code) {
        return new ResourceNotFoundException("Produit", "code", code, "PRODUCT_NOT_FOUND");
    }

    public static ResourceNotFoundException forIngredient(Long id) {
        return new ResourceNotFoundException("Ingredient", "id", id, "INGREDIENT_NOT_FOUND");
    }

    public static ResourceNotFoundException forIngredientByNom(String nom) {
        return new ResourceNotFoundException("Ingredient", "nom", nom, "INGREDIENT_NOT_FOUND");
    }

    public static ResourceNotFoundException forRecette(Long id) {
        return new ResourceNotFoundException("Recette", "id", id, "RECIPE_NOT_FOUND");
    }

    public static ResourceNotFoundException forCommande(Long id) {
        return new ResourceNotFoundException("Commande", "id", id, "ORDER_NOT_FOUND");
    }

    public static ResourceNotFoundException forCommandeByNumero(String numero) {
        return new ResourceNotFoundException("Commande", "numero", numero, "ORDER_NOT_FOUND");
    }

    public static ResourceNotFoundException forStock(Long id) {
        return new ResourceNotFoundException("Stock", "id", id, "STOCK_NOT_FOUND");
    }

    public static ResourceNotFoundException forStockByIngredient(Long ingredientId) {
        return new ResourceNotFoundException("Stock", "ingredient_id", ingredientId, "STOCK_NOT_FOUND");
    }

    public static ResourceNotFoundException forFournisseur(Long id) {
        return new ResourceNotFoundException("Fournisseur", "id", id, "SUPPLIER_NOT_FOUND");
    }

    public static ResourceNotFoundException forCategorie(Long id) {
        return new ResourceNotFoundException("Catégorie", "id", id, "CATEGORY_NOT_FOUND");
    }

    public static ResourceNotFoundException forCategorieByNom(String nom) {
        return new ResourceNotFoundException("Catégorie", "nom", nom, "CATEGORY_NOT_FOUND");
    }

    public static ResourceNotFoundException forClient(Long id) {
        return new ResourceNotFoundException("Client", "id", id, "CLIENT_NOT_FOUND");
    }

    public static ResourceNotFoundException forClientByEmail(String email) {
        return new ResourceNotFoundException("Client", "email", email, "CLIENT_NOT_FOUND");
    }

    public static ResourceNotFoundException forCommandeProduit(Long commandeId, Long produitId) {
        return new ResourceNotFoundException(
                "Produit de commande",
                String.format("commande_id=%s, produit_id=%s", commandeId, produitId),
                null,
                "ORDER_PRODUCT_NOT_FOUND"
        );
    }

    public static ResourceNotFoundException forRecetteIngredient(Long recetteId, Long ingredientId) {
        return new ResourceNotFoundException(
                "Ingredient de recette",
                String.format("recette_id=%s, ingredient_id=%s", recetteId, ingredientId),
                null,
                "RECIPE_INGREDIENT_NOT_FOUND"
        );
    }

    // Methods for building custom messages
    public static ResourceNotFoundException withCustomMessage(String resource, String message) {
        return new ResourceNotFoundException(String.format("%s : %s", resource, message));
    }

    public static ResourceNotFoundException forEntity(Class<?> entityClass, Long id) {
        String resourceName = entityClass.getSimpleName();
        return new ResourceNotFoundException(resourceName, "id", id);
    }

    public static ResourceNotFoundException forEntity(Class<?> entityClass, String fieldName, Object fieldValue) {
        String resourceName = entityClass.getSimpleName();
        return new ResourceNotFoundException(resourceName, fieldName, fieldValue);
    }
}
