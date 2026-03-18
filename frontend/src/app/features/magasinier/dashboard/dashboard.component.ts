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
  styleUrls: ['./dashboard.component.css']
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
      if (ingredients && Array.isArray(ingredients)) {
        this.ingredientsCritiques = ingredients.slice(0, 5);
      } else {
        this.ingredientsCritiques = [];
      }
    });

    this.purchaseService.getAll().subscribe(cmds => {
      if (cmds && Array.isArray(cmds)) {
        this.commandesAchat = cmds.slice(0, 5).map(c => ({
          id: c.id,
          fournisseurNom: c.nomFournisseur ?? 'Fournisseur inconnu',
          dateCommande: c.dateCommande,
          statut: c.statut
        }));
      } else {
        this.commandesAchat = [];
      }
    });
  }
}
