import { Component, OnInit, inject, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive, Router, NavigationEnd } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { SidebarService } from '../../core/services/sidebar.service';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent implements OnInit {
  private authService = inject(AuthService);
  public sidebarService = inject(SidebarService);
  private router = inject(Router);
  userRole: string | null = null;

  get userDashboardRoute(): string {
    const role = this.userRole || 'EMPLOYE';
    return `/${role.toLowerCase()}/dashboard`;
  }

  ngOnInit() {
    this.authService.currentUser$.subscribe(() => {
      this.userRole = this.authService.getRole();
    });

    // Auto-close sidebar on navigation (mobile)
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe(() => {
      if (window.innerWidth <= 1024) {
        this.sidebarService.close();
      }
    });
  }

  toggleSidebar() {
    this.sidebarService.toggle();
  }

  closeSidebar() {
    this.sidebarService.close();
  }

  @HostListener('window:resize')
  onResize() {
    if (window.innerWidth > 1024) {
      this.sidebarService.close(); // Reset on desktop
    }
  }

  logout() {
    this.authService.logout();
  }
}
