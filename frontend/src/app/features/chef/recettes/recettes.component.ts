import { Component, OnInit, inject, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RecetteService } from '../../../core/services/recette.service';
import { Recette } from '../../../core/models/recette.model';
import { FormatPricePipe } from '../../../shared/pipes/format-price.pipe';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';
import { ConfirmDirective } from '../../../shared/directives/confirm.directive';
import { NgxPaginationModule } from 'ngx-pagination';
import { NotificationService } from '../../../core/services/notification.service';

import { RecipeFormModalComponent } from './recipe-form-modal/recipe-form-modal.component';

@Component({
  selector: 'app-recettes',
  standalone: true,
  imports: [
    CommonModule, 
    FormsModule, 
    FormatPricePipe, 
    EmptyStateComponent, 
    ConfirmDirective, 
    NgxPaginationModule,
    RecipeFormModalComponent
  ],
  templateUrl: './recettes.component.html',
  styles: [`
    .recipe-management {
      min-height: 100vh;
      animation: fadeIn 0.5s ease-out;
    }
    .recipe-card {
      transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
      cursor: pointer;
      border: 1px solid rgba(255, 255, 255, 0.4);
    }
    .recipe-card:hover {
      transform: translateY(-8px);
      box-shadow: 0 15px 35px rgba(139, 69, 19, 0.1) !important;
    }
    .recipe-img-container {
      height: 180px;
      overflow: hidden;
      border-radius: 18px;
      background: #FDF4E3;
      display: flex;
      align-items: center;
      justify-content: center;
      color: var(--marron-chocolat);
      position: relative;
    }
    .prep-tag {
      position: absolute;
      top: 15px;
      right: 15px;
      background: rgba(255, 255, 255, 0.9);
      backdrop-filter: blur(4px);
      padding: 5px 12px;
      border-radius: 50px;
      font-size: 0.75rem;
      font-weight: bold;
      color: var(--marron-fonce);
      box-shadow: 0 4px 10px rgba(0,0,0,0.05);
    }
    .cost-badge {
      font-size: 0.9rem;
      color: var(--dore-sombre);
    }
    .btn-action-small {
      width: 32px;
      height: 32px;
      border-radius: 8px;
      display: flex;
      align-items: center;
      justify-content: center;
      background: white;
      border: 1px solid rgba(0,0,0,0.05);
      transition: all 0.2s;
    }
    .btn-action-small:hover {
      background: var(--beige-creme);
      color: var(--marron-chocolat);
    }
  `]
})
export class RecettesComponent implements OnInit {
  private recetteService = inject(RecetteService);
  private notification = inject(NotificationService);
  @ViewChild('recipeModal') recipeModal!: RecipeFormModalComponent;

  recettes: Recette[] = [];
  filteredRecettes: Recette[] = [];
  searchTerm: string = '';
  sortBy: string = 'nom';
  p: number = 1;

  ngOnInit() {
    this.loadRecettes();
  }

  loadRecettes() {
    this.recetteService.getAll().subscribe({
      next: (data) => {
        this.recettes = data;
        this.applyFilters();
      }
    });
  }

  applyFilters() {
    this.filteredRecettes = this.recettes.filter(r => 
      r.nom.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
      r.description?.toLowerCase().includes(this.searchTerm.toLowerCase())
    );

    if (this.sortBy === 'coût') {
      this.filteredRecettes.sort((a, b) => a.coutTotal - b.coutTotal);
    } else if (this.sortBy === 'temps') {
      this.filteredRecettes.sort((a, b) => (a.tempsPreparation || 0) - (b.tempsPreparation || 0));
    } else {
      this.filteredRecettes.sort((a, b) => a.nom.localeCompare(b.nom));
    }
    this.p = 1;
  }

  openRecipeModal(recette?: Recette) {
    this.recipeModal.open(recette);
  }

  viewDetails(recette: Recette) {
    console.log('View details for:', recette);
  }

  editRecipe(recette: Recette) {
    this.openRecipeModal(recette);
  }

  deleteRecipe(recette: Recette) {
    this.recetteService.delete(recette.id).subscribe({
      next: () => {
        this.recettes = this.recettes.filter(r => r.id !== recette.id);
        this.applyFilters();
        this.notification.success('Recette supprimée.', 'Succès');
      }
    });
  }
}
