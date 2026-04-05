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
  commandesAujourdhui: any[] = [];
  stockParCategorie: { [key: string]: number } = {};
  valeurTotaleStock = 0;
  today = new Date();

  ngOnInit() {
    this.dashboardService.getMagasinierStats().subscribe(stats => {
      this.stats = stats;
    });

    this.ingredientService.getAll().subscribe(ingredients => {
      if (ingredients && Array.isArray(ingredients)) {
        this.valeurTotaleStock = ingredients.reduce((acc, curr) => acc + (curr.quantiteStock * curr.prixUnitaire), 0);
        
        // Group by category
        this.stockParCategorie = ingredients.reduce((acc: any, curr) => {
          const cat = curr.categorie || 'Autres';
          acc[cat] = (acc[cat] || 0) + curr.quantiteStock;
          return acc;
        }, {});

        // Detailed critical ingredients (slice top 5)
        this.ingredientsCritiques = ingredients
          .filter(i => i.quantiteStock <= i.quantiteMinimum)
          .slice(0, 5);
      }
    });

    this.purchaseService.getAll().subscribe(cmds => {
      if (cmds && Array.isArray(cmds)) {
        const todayStr = this.today.toISOString().split('T')[0];
        this.commandesAujourdhui = cmds
          .filter(c => c.dateLivraisonPrevue && c.dateLivraisonPrevue.startsWith(todayStr))
          .map(c => ({
            id: c.id,
            fournisseurNom: c.fournisseurNom ?? 'Fournisseur inconnu',
            total: c.montantTotal,
            statut: c.statut,
            date: c.dateCommande
          }));
      } else {
        this.commandesAujourdhui = [];
      }
    });
  }
}
