import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { DashboardService } from '../../../core/services/dashboard.service';
import { EmployeStats } from '../../../core/models/dashboard.model';
import { VenteService } from '../../../core/services/vente.service';
import { Vente } from '../../../core/models/vente.model';
import { FormatPricePipe } from '../../../shared/pipes/format-price.pipe';
import { NotificationService } from '../../../core/services/notification.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-employe-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, FormatPricePipe],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class EmployeDashboardComponent implements OnInit {
  private dashboardService = inject(DashboardService);
  private venteService = inject(VenteService);
  private authService = inject(AuthService);
  private notification = inject(NotificationService);

  stats?: EmployeStats;
  derniereVente?: Vente;
  tasks: any[] = [];
  today = new Date();

  ngOnInit() {
    this.dashboardService.getEmployeStats().subscribe(stats => {
      this.stats = stats;
    });

    this.venteService.getAll().subscribe(ventes => {
      if (ventes && Array.isArray(ventes) && ventes.length > 0) {
        this.derniereVente = ventes[0];
      }
    });

    // loadTasks() removed as service doesn't exist
  }

  // completeTask removed
}
