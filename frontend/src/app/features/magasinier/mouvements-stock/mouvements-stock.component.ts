import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { IngredientService } from '../../../core/services/ingredient.service';
import { Ingredient, MouvementStock } from '../../../core/models/ingredient.model';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';
import { NgxPaginationModule } from 'ngx-pagination';

@Component({
  selector: 'app-mouvements-stock',
  standalone: true,
  imports: [CommonModule, FormsModule, EmptyStateComponent, NgxPaginationModule],
  templateUrl: './mouvements-stock.component.html',
  styles: [`
    .mouvements-page { min-height: 100vh; animation: fadeIn 0.5s ease-out; }
    .type-entree { color: #2ecc71; }
    .type-sortie { color: #e74c3c; }
    .type-perte { color: #e67e22; }
    .type-ajustement { color: #3498db; }
  `]
})
export class MouvementsStockComponent implements OnInit {
  private ingredientService = inject(IngredientService);
  
  mouvements: MouvementStock[] = [];
  filteredMouvements: MouvementStock[] = [];
  ingredients: Ingredient[] = [];
  
  p: number = 1;
  filterType: string = 'ALL';
  selectedIngredientId: number | null = null;

  ngOnInit() {
    this.loadData();
  }

  loadData() {
    this.ingredientService.getMouvements().subscribe(data => {
      this.mouvements = data;
      this.applyFilters();
    });
    this.ingredientService.getAll().subscribe(data => this.ingredients = data);
  }

  applyFilters() {
    this.filteredMouvements = this.mouvements.filter(m => {
      const matchType = this.filterType === 'ALL' || m.type === this.filterType;
      const matchIng = !this.selectedIngredientId || m.ingredientId === this.selectedIngredientId;
      return matchType && matchIng;
    });
  }

  getIngredientName(id: number): string {
    return this.ingredients.find(i => i.id === id)?.nom || 'Inconnu';
  }
}
