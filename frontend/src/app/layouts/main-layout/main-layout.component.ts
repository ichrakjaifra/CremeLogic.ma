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
    }
    .main-content {
      flex: 1;
      padding: 30px 40px !important;
      animation: fadeIn 0.5s ease-out;
    }
  `]
})
export class MainLayoutComponent {}
