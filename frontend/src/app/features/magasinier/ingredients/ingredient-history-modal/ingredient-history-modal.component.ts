import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Ingredient, MouvementStock } from '../../../../core/models/ingredient.model';
import { IngredientService } from '../../../../core/services/ingredient.service';

@Component({
  selector: 'app-ingredient-history-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './ingredient-history-modal.component.html',
  styles: [`
    .modal.show { background: rgba(0,0,0,0.5); }
    .color-marron { color: var(--marron-chocolat); }
    .table-history {
      font-size: 0.9rem;
    }
    .badge-ENTREE { background-color: #d1fae5; color: #065f46; }
    .badge-SORTIE { background-color: #fee2e2; color: #991b1b; }
    .badge-PERDU { background-color: #fef3c7; color: #92400e; }
    .badge-DETRUIT { background-color: #ffedd5; color: #7c2d12; }
    .badge-AJUSTEMENT { background-color: #e0f2fe; color: #075985; }
  `]
})
export class IngredientHistoryModalComponent {
  private ingredientService = inject(IngredientService);

  isOpen = false;
  loading = false;
  ingredient?: Ingredient;
  mouvements: MouvementStock[] = [];

  open(ingredient: Ingredient) {
    this.isOpen = true;
    this.ingredient = ingredient;
    this.loadHistory(ingredient.id);
  }

  loadHistory(id: number) {
    this.loading = true;
    this.ingredientService.getMouvements(id).subscribe({
      next: (data) => {
        this.mouvements = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading history:', err);
        this.loading = false;
      }
    });
  }

  close() {
    this.isOpen = false;
  }
}
