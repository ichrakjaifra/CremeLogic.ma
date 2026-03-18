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
  styleUrls: ['./dashboard.component.css']
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
      if (ordres && Array.isArray(ordres)) {
        this.ordresRecents = ordres.slice(0, 5).map(o => ({
          ...o,
          produitNom: o.produitNom ?? 'Produit inconnu',
          recetteVersion: 'V1.0'
        }));
      } else {
        this.ordresRecents = [];
      }
    });
  }
}
