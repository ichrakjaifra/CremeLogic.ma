import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { SidebarComponent } from '../sidebar/sidebar.component';
import { NavbarComponent } from '../navbar/navbar.component';
import { FooterComponent } from '../footer/footer.component';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [CommonModule, RouterOutlet, SidebarComponent, NavbarComponent, FooterComponent],
  templateUrl: './main-layout.component.html',
  styles: [`
    .main-container {
      display: flex;
      min-height: 100vh;
    }
    .content-wrapper {
      flex: 1;
      display: flex;
      flex-direction: column;
      margin-left: 280px;
      height: 100vh;
      overflow-y: auto;
      background: transparent;
      transition: margin-left 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    }
    .main-content {
      flex: 1;
      padding: 30px 40px !important;
      animation: fadeIn 0.5s ease-out;
    }

    /* Tablet & Mobile: no sidebar margin */
    @media (max-width: 1024px) {
      .content-wrapper {
        margin-left: 0;
      }
      .main-content {
        padding: 20px !important;
      }
    }

    @media (max-width: 430px) {
      .main-content {
        padding: 12px !important;
      }
    }
  `]
})
export class MainLayoutComponent {}
