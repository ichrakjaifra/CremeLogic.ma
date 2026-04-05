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
  styleUrls: ['./recettes.component.css']
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
    if (!this.recettes || !Array.isArray(this.recettes)) {
      this.filteredRecettes = [];
      return;
    }
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
