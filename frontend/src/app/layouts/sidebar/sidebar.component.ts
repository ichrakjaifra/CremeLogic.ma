import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
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
