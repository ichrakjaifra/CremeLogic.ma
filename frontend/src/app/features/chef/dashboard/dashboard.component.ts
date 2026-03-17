import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DashboardService } from '../../../core/services/dashboard.service';
import { ChefStats } from '../../../core/models/dashboard.model';
import { OrdreProductionService } from '../../../core/services/ordre-production.service';
import { OrdreProduction } from '../../../core/models/ordre-production.model';

@Component({
  selector: 'app-chef-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
  styles: [`
    .chef-dashboard { background-color: var(--bg-global); min-height: 100vh; }
    .kpi-icon { width: 50px; height: 50px; border-radius: 12px; display: flex; align-items: center; justify-content: center; font-size: 1.5rem; }
    .bg-dore-soft { background-color: #fffbeb; }
    .bg-success-soft { background-color: #f0fdf4; }
    .bg-warning-soft { background-color: #fffbeb; }
    .bg-danger-soft { background-color: #fef2f2; }
    .bg-info-soft { background-color: #eff6ff; }
    .alert-warning-soft { background-color: #fffbeb; }
    .extra-small { font-size: 0.75rem; }
    .bg-beige-light { background-color: #fff9f0; }
  `]
})
export class ChefDashboardComponent implements OnInit {
  private dashboardService = inject(DashboardService);
  private ordreService = inject(OrdreProductionService);

  stats?: ChefStats;
  ordresRecents: any[] = []; // Using any for simplicity in mockup, should be OrdreProduction
  today = new Date();

  ngOnInit() {
    this.dashboardService.getChefStats().subscribe(stats => {
      this.stats = stats;
    });

    this.ordreService.getAll().subscribe(ordres => {
      // Mocking some display data for the table
      this.ordresRecents = ordres.slice(0, 5).map(o => ({
        ...o,
        produitNom: 'Gâteau Chocolat', // Mocking name until expansion logic implemented
        recetteVersion: 'V2.1'
      }));
    });
  }
}
