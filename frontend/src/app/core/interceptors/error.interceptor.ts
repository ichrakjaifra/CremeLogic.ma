import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { NotificationService } from '../services/notification.service';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const notification = inject(NotificationService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      let errorMessage = 'Une erreur est survenue';

      if (error.status === 401) {
        errorMessage = 'Session expirée ou identifiants incorrects';
        authService.logout();
      } else if (error.status === 403) {
        errorMessage = "Vous n'avez pas la permission d'accéder à cette ressource";
      } else if (error.status === 404) {
        errorMessage = 'Ressource non trouvée';
      } else if (error.error && error.error.message) {
        errorMessage = error.error.message;
      }

      notification.error(errorMessage, 'Erreur');
      return throwError(() => error);
    })
  );
};
