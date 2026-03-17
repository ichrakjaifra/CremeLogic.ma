import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { IngredientService } from '../../../core/services/ingredient.service';
import { AuthService } from '../../../core/services/auth.service';
import { Ingredient } from '../../../core/models/ingredient.model';
import { FormatPricePipe } from '../../../shared/pipes/format-price.pipe';
import { StockStatusPipe } from '../../../shared/pipes/stock-status.pipe';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';
import { NgxPaginationModule } from 'ngx-pagination';
import { NotificationService } from '../../../core/services/notification.service';

@Component({
  selector: 'app-ingredients',
  standalone: true,
  imports: [CommonModule, FormsModule, FormatPricePipe, StockStatusPipe, EmptyStateComponent, NgxPaginationModule],
  templateUrl: './ingredients.component.html',
  styles: [`
    .ingredients-page {
      min-height: 100vh;
      animation: fadeIn 0.5s ease-out;
    }
    .search-box {
      border-radius: 12px;
      overflow: hidden;
      box-shadow: 0 4px 15px rgba(139, 69, 19, 0.05);
      background: white;
    }
    .custom-table thead th {
      background: rgba(139, 69, 19, 0.03);
      padding: 15px 20px;
      color: var(--marron-chocolat);
      font-size: 0.8rem;
      text-uppercase: uppercase;
      letter-spacing: 1px;
      border-bottom: 2px solid rgba(139, 69, 19, 0.05);
    }
    .custom-table tbody tr {
      transition: all 0.2s ease;
      cursor: pointer;
    }
    .custom-table tbody tr:hover {
      background: rgba(253, 244, 227, 0.3);
    }
    .btn-action {
      width: 36px;
      height: 36px;
      border-radius: 10px;
      display: flex;
      align-items: center;
      justify-content: center;
      border: none;
      transition: all 0.2s ease;
      background: white;
      color: var(--marron-chocolat);
      box-shadow: 0 4px 8px rgba(0,0,0,0.05);
    }
    .btn-action:hover {
      transform: translateY(-2px);
      box-shadow: 0 6px 12px rgba(0,0,0,0.1);
    }
    .btn-action.edit:hover { color: #3498db; }
    .btn-action.key:hover { color: #f1c40f; }
    
    .bg-chocolate-light { background: #FDF4E3; }
    
    .custom-switch {
      width: 2.8em !important;
      height: 1.4em !important;
      cursor: pointer;
    }
    .custom-switch:checked {
      background-color: #2ecc71 !important;
      border-color: #2ecc71 !important;
    }
  `]
})
export class IngredientsComponent implements OnInit {
  private ingredientService = inject(IngredientService);
  private authService = inject(AuthService);
  private notification = inject(NotificationService);

  ingredients: Ingredient[] = [];
  filteredIngredients: Ingredient[] = [];
  searchTerm: string = '';
  filterAlerte: string = 'ALL';
  p: number = 1;
  userRole: string | null = null;

  ngOnInit() {
    this.userRole = this.authService.getRole();
    this.loadIngredients();
  }

  canEdit(): boolean {
    return ['ADMIN', 'MAGASINIER'].includes(this.userRole || '');
  }

  loadIngredients() {
    this.ingredientService.getAll().subscribe(data => {
      this.ingredients = data;
      this.applyFilters();
    });
  }

  applyFilters() {
    this.filteredIngredients = this.ingredients.filter(ing => {
      const matchSearch = !this.searchTerm || ing.nom.toLowerCase().includes(this.searchTerm.toLowerCase());
      let matchAlerte = true;
      if (this.filterAlerte === 'BAS') {
        matchAlerte = ing.quantiteStock > 0 && ing.quantiteStock <= ing.quantiteMinimum;
      } else if (this.filterAlerte === 'RUPTURE') {
        matchAlerte = ing.quantiteStock <= 0;
      }
      return matchSearch && matchAlerte;
    });
    this.p = 1;
  }

  openIngredientModal(ing?: Ingredient) {
    this.notification.info('Gestion détaillée des ingrédients en cours de développement.', 'Info');
  }

  openMouvementModal() {
     this.notification.info('Saisie de mouvement de stock en cours de développement.', 'Info');
  }

  editIngredient(ing: Ingredient) {
    this.openIngredientModal(ing);
  }

  viewHistory(ing: Ingredient) {
    console.log('Historique pour:', ing);
  }
}
