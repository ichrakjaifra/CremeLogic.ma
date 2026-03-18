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
  styleUrls: ['./ingredients.component.css']
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
    if (!this.ingredients || !Array.isArray(this.ingredients)) {
      this.filteredIngredients = [];
      return;
    }
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
