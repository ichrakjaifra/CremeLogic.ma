import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './sidebar.component.html',
  styles: [`
    .sidebar {
      width: 280px;
      height: 100vh;
      background: linear-gradient(180deg, var(--marron-chocolat) 0%, var(--marron-fonce) 100%);
      box-shadow: 10px 0 30px rgba(139, 69, 19, 0.15);
      position: fixed;
      left: 0;
      top: 0;
      z-index: 1050;
      border-right: 1px solid rgba(255, 255, 255, 0.1);
      transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    }
    .sidebar-logo {
      max-width: 140px;
      margin: 15px auto;
      filter: drop-shadow(0 4px 10px rgba(0,0,0,0.2));
    }
    .nav-link {
      padding: 14px 24px;
      margin: 6px 15px;
      border-radius: 12px;
      transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
      font-family: 'Montserrat', sans-serif;
      font-weight: 500;
      display: flex;
      align-items: center;
      color: rgba(255, 255, 255, 0.8) !important;
      border: 1px solid transparent;
    }
    .nav-link:hover {
      background: rgba(255, 255, 255, 0.1);
      color: white !important;
      transform: translateX(5px);
    }
    .nav-link.active {
      background: linear-gradient(135deg, var(--dore-clair) 0%, var(--dore-sombre) 100%);
      color: white !important;
      box-shadow: 0 8px 20px rgba(212, 175, 55, 0.3);
      border: 1px solid rgba(255, 255, 255, 0.2);
    }
    .nav-link i {
      width: 20px;
      font-size: 1.1rem;
    }
    .logout-container {
      margin-top: auto;
      padding: 20px 15px;
      border-top: 1px solid rgba(255, 255, 255, 0.1);
    }
    .logout-link {
      padding: 14px 24px;
      cursor: pointer;
      color: rgba(255, 255, 255, 0.7) !important;
      transition: all 0.3s ease;
      display: flex;
      align-items: center;
      border-radius: 12px;
    }
    .logout-link:hover {
      background: rgba(231, 76, 60, 0.15);
      color: #FF7675 !important;
      transform: translateX(5px);
    }
    hr {
      border-color: rgba(255, 255, 255, 0.1);
      margin: 1rem 1.5rem;
    }
  `]
})
export class SidebarComponent implements OnInit {
  private authService = inject(AuthService);
  userRole: string | null = null;

  get userDashboardRoute(): string {
    const role = this.userRole || 'EMPLOYE';
    return `/${role.toLowerCase()}/dashboard`;
  }

  ngOnInit() {
    this.authService.currentUser$.subscribe(() => {
      this.userRole = this.authService.getRole();
    });
  }

  logout() {
    this.authService.logout();
  }
}
