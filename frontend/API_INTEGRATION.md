# API Integration Documentation

## Backend Endpoints Used

### Authentication
- `POST /auth/login`: User authentication.
- `POST /auth/logout`: Session termination.

### Users (Admin)
- `GET /users`: List all users.
- `POST /users`: Create new user.
- `PUT /users/{id}`: Update user.
- `PATCH /users/{id}/toggle-actif`: Enable/Disable user.
- `DELETE /users/{id}`: Delete user.

### Ingredients (Magasinier)
- `GET /ingredients`: Full inventory.
- `GET /ingredients/stock-faible`: Critical stocks.
- `POST /ingredients/{id}/ajuster-stock`: Manual adjustment.

### Recettes (Chef)
- `GET /recettes`: All recipes.
- `POST /recettes`: New creation.

### Ventes (Employé)
- `POST /ventes`: Record transaction.
- `GET /ventes/ma-journee`: Current user sales.

## Data Mapping Notes
- All monetary values use `BigDecimal` on backend and `number` on frontend.
- Dates are ISO-8601 strings.
- JWT is stored in `localStorage`.
