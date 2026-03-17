import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { DashboardService } from '../../../core/services/dashboard.service';
import { MagasinierStats } from '../../../core/models/dashboard.model';
import { IngredientService } from '../../../core/services/ingredient.service';
import { CommandeAchatService } from '../../../core/services/commande-achat.service';
import { Ingredient } from '../../../core/models/ingredient.model';
import { CommandeAchat } from '../../../core/models/commande-achat.model';

@Component({
  selector: 'app-magasinier-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.component.html',
  styles: [`
    .magasinier-dashboard { background-color: var(--bg-global); min-height: 100vh; }
    .kpi-icon { width: 50px; height: 50px; border-radius: 12px; display: flex; align-items: center; justify-content: center; font-size: 1.5rem; }
    .bg-danger-soft { background-color: #fef2f2; }
    .bg-warning-soft { background-color: #fffbeb; }
    .bg-success-soft { background-color: #f0fdf4; }
    .bg-info-soft { background-color: #eff6ff; }
    .alert-danger-soft { background-color: #fff1f2; }
    .extra-small { font-size: 0.75rem; }
  `]
})
export class MagasinierDashboardComponent implements OnInit {
  private dashboardService = inject(DashboardService);
  private ingredientService = inject(IngredientService);
  private purchaseService = inject(CommandeAchatService);

  stats?: MagasinierStats;
  ingredientsCritiques: Ingredient[] = [];
  commandesAchat: any[] = []; // Simplified for mock
  today = new Date();

  ngOnInit() {
    this.dashboardService.getMagasinierStats().subscribe(stats => {
      this.stats = stats;
    });

    this.ingredientService.getStockFaible().subscribe(ingredients => {
      this.ingredientsCritiques = ingredients.slice(0, 5);
    });

    this.purchaseService.getAll().subscribe(cmds => {
      this.commandesAchat = cmds.slice(0, 5).map(c => ({
        id: c.id,
        fournisseurNom: 'Fournisseur Central', // Mock
        dateCommande: c.dateCommande,
        statut: c.statut
      }));
    });
  }
}
