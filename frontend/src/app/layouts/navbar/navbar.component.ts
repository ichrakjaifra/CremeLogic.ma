import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { User } from '../../core/models/user.model';
import { RoleLabelPipe } from '../../shared/pipes/role-label.pipe';
import { AlerteService } from '../../core/services/alerte.service';
import { WebsocketService } from '../../core/services/websocket.service';
import { NotificationService } from '../../core/services/notification.service';
import { Alerte } from '../../core/models/alerte.model';
import { TypeAlerte } from '../../core/enums/type-alerte.enum';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink, RoleLabelPipe],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {
  private authService = inject(AuthService);
  private alerteService = inject(AlerteService);
  private wsService = inject(WebsocketService);
  private toastService = inject(NotificationService);

  currentUser: User | null = null;
  alertes$ = this.alerteService.alertes$;
  unreadCount$ = this.alerteService.unreadCount$;

  ngOnInit() {
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
      if (user) {
        this.setupNotifications();
      }
    });
  }

  setupNotifications() {
    this.alerteService.loadInitialAlertes();
    this.wsService.connect();
    
    // S'abonner aux notifications générales
    this.wsService.subscribe('/topic/notifications', (alerte: Alerte) => {
      if (this.shouldShowNotification(alerte)) {
        this.alerteService.addAlerte(alerte);
        this.toastService.info(alerte.description, alerte.titre);
      }
    });
  }

  shouldShowNotification(alerte: Alerte): boolean {
    if (!this.currentUser) return false;
    const role = this.currentUser.role;
    
    // Logique de filtrage par rôle
    switch (alerte.type) {
      case TypeAlerte.STOCK_FAIBLE:
        return role === 'MAGASINIER' || role === 'ADMIN';
      case TypeAlerte.COMMANDE_RETARD:
        return role === 'ADMIN' || role === 'MAGASINIER';
      case TypeAlerte.PRODUCTION_TERMINEE:
        return role === 'CHEF' || role === 'EMPLOYE';
      case TypeAlerte.PROBLEME_SIGNALE:
        return role === 'CHEF' || role === 'ADMIN';
      case TypeAlerte.NOUVELLE_COMMANDE:
        return role === 'CHEF' || role === 'ADMIN';
      default:
        return true; // Par défaut on montre tout (ou à affiner)
    }
  }

  markAsRead(id: number, event: Event) {
    event.stopPropagation();
    this.alerteService.resoudreAlerte(id).subscribe();
  }

  markAllAsRead() {
    this.alerteService.resoudreToutes();
  }

  getIconForType(type: TypeAlerte): string {
    switch (type) {
        case TypeAlerte.STOCK_FAIBLE: return 'fa-boxes-stacked text-warning';
        case TypeAlerte.PRODUCTION_TERMINEE: return 'fa-check-circle text-success';
        case TypeAlerte.NOUVELLE_COMMANDE: return 'fa-shopping-bag text-info';
        case TypeAlerte.PROBLEME_SIGNALE: return 'fa-exclamation-triangle text-danger';
        default: return 'fa-bell text-primary';
    }
  }

  logout() {
    this.authService.logout();
  }
}
