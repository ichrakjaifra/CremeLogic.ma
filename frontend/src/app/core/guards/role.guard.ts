import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { NotificationService } from '../services/notification.service';

export const roleGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const notification = inject(NotificationService);

  const expectedRoles = route.data['roles'] as Array<string>;
  const userRole = authService.getRole();

  if (!userRole || !expectedRoles.includes(userRole)) {
    // If user is ADMIN, they should technically have access to everything
    if (userRole === 'ADMIN') {
      return true;
    }
    
    notification.error("Vous n'avez pas la permission d'accéder à cette page", "Accès refusé");
    router.navigate(['/auth/login']);
    return false;
  }

  return true;
};
