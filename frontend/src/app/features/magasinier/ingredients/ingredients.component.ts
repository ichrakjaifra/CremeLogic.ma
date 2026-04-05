import { Component, OnInit, inject, ViewChild } from '@angular/core';
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
import { IngredientFormModalComponent } from './ingredient-form-modal/ingredient-form-modal.component';
import { StockMouvementModalComponent } from './stock-mouvement-modal/stock-mouvement-modal.component';
import { IngredientHistoryModalComponent } from './ingredient-history-modal/ingredient-history-modal.component';

@Component({
  selector: 'app-ingredients',
  standalone: true,
  imports: [
    CommonModule, 
    FormsModule, 
    FormatPricePipe, 
    StockStatusPipe, 
    EmptyStateComponent, 
    NgxPaginationModule,
    IngredientFormModalComponent,
    StockMouvementModalComponent,
    IngredientHistoryModalComponent
  ],
  templateUrl: './ingredients.component.html',
  styleUrls: ['./ingredients.component.css']
})
export class IngredientsComponent implements OnInit {
  private ingredientService = inject(IngredientService);
  private authService = inject(AuthService);
  private notification = inject(NotificationService);

  @ViewChild('ingredientModal') ingredientModal!: IngredientFormModalComponent;
  @ViewChild('mouvementModal') mouvementModal!: StockMouvementModalComponent;
  @ViewChild('historyModal') historyModal!: IngredientHistoryModalComponent;

  ingredients: Ingredient[] = [];
  filteredIngredients: Ingredient[] = [];
  searchTerm: string = '';
  filterAlerte: string = 'ALL';
  p: number = 1;
  userRole: string | null = null;
  valeurStockTotal: number = 0;

  ngOnInit() {
    this.userRole = this.authService.getRole();
    this.loadIngredients();
    this.loadValeurStock();
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

  loadValeurStock() {
    this.ingredientService.getValeurStockTotal().subscribe(val => {
      this.valeurStockTotal = val;
    });
  }

  applyFilters() {
    if (!this.ingredients || !Array.isArray(this.ingredients)) {
      this.filteredIngredients = [];
      return;
    }
    this.filteredIngredients = this.ingredients.filter(ing => {
      const matchSearch = !this.searchTerm || 
          ing.nom.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
          ing.codeIngredient?.toLowerCase().includes(this.searchTerm.toLowerCase());
      
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
    this.ingredientModal.open(ing);
  }

  openMouvementModal(ing?: Ingredient) {
    this.mouvementModal.open(this.ingredients, ing?.id);
  }

  editIngredient(ing: Ingredient) {
    this.openIngredientModal(ing);
  }

  viewHistory(ing: Ingredient) {
    this.historyModal.open(ing);
  }

  deleteIngredient(ing: Ingredient) {
    if (confirm(`Êtes-vous sûr de vouloir supprimer l'ingrédient "${ing.nom}" ?`)) {
      this.ingredientService.delete(ing.id).subscribe({
        next: () => {
          this.notification.success('Ingrédient supprimé.', 'Succès');
          this.loadIngredients();
          this.loadValeurStock();
        },
        error: (err) => {
          this.notification.error('Impossible de supprimer l\'ingrédient. Il est probablement utilisé.', 'Erreur');
        }
      });
    }
  }

  onDataChanged() {
    this.loadIngredients();
    this.loadValeurStock();
  }
}
