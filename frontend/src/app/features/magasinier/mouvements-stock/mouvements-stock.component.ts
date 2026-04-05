import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { IngredientService } from '../../../core/services/ingredient.service';
import { Ingredient, MouvementStock } from '../../../core/models/ingredient.model';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';
import { NgxPaginationModule } from 'ngx-pagination';
import { NotificationService } from '../../../core/services/notification.service';
import { FormatPricePipe } from '../../../shared/pipes/format-price.pipe';

@Component({
  selector: 'app-mouvements-stock',
  standalone: true,
  imports: [
    CommonModule, 
    FormsModule, 
    ReactiveFormsModule, 
    EmptyStateComponent, 
    NgxPaginationModule, 
    FormatPricePipe
  ],
  templateUrl: './mouvements-stock.component.html',
  styles: [`
    .mouvements-page { min-height: 100vh; animation: fadeIn 0.5s ease-out; }
    .bg-success-light { background: #E8F5E9; }
    .bg-danger-light { background: #FFEBEE; }
    .bg-warning-light { background: #FFF3E0; }
  `]
})
export class MouvementsStockComponent implements OnInit {
  private ingredientService = inject(IngredientService);
  private fb = inject(FormBuilder);
  private notification = inject(NotificationService);
  
  mouvements: MouvementStock[] = [];
  filteredMouvements: MouvementStock[] = [];
  ingredients: Ingredient[] = [];
  
  p: number = 1;
  filterType: string = 'ALL';
  selectedIngredientId: number | null = null;
  dateDebut: string = '';
  dateFin: string = '';

  showModal: boolean = false;
  mouvementForm!: FormGroup;
  loading: boolean = false;

  stats = {
    totalEntrees: 0,
    totalSorties: 0,
    totalPertes: 0
  };

  ngOnInit() {
    this.initForm();
    this.loadData();
  }

  initForm() {
    this.mouvementForm = this.fb.group({
      ingredientId: [null, Validators.required],
      type: ['ENTREE', Validators.required],
      quantite: [null, [Validators.required, Validators.min(0.01)]],
      raison: ['', Validators.required],
      coutUnitaire: [null]
    });
  }

  loadData() {
    this.ingredientService.getMouvements().subscribe(data => {
      this.mouvements = data || [];
      this.applyFilters();
    });
    this.ingredientService.getAll().subscribe(data => this.ingredients = data || []);
  }

  applyFilters() {
    this.filteredMouvements = (this.mouvements || []).filter(m => {
      const matchType = this.filterType === 'ALL' || m.type === this.filterType;
      const matchIng = !this.selectedIngredientId || Number(m.ingredientId) === Number(this.selectedIngredientId);
      
      let matchDate = true;
      if (this.dateDebut || this.dateFin) {
        const mDate = new Date(m.dateMouvement).toISOString().split('T')[0];
        if (this.dateDebut && mDate < this.dateDebut) matchDate = false;
        if (this.dateFin && mDate > this.dateFin) matchDate = false;
      }
      
      return matchType && matchIng && matchDate;
    });
    this.calculateStats();
  }

  calculateStats() {
    this.stats.totalEntrees = this.filteredMouvements
      .filter(m => m.type === 'ENTREE')
      .reduce((acc, m) => acc + m.quantite, 0);
    this.stats.totalSorties = this.filteredMouvements
      .filter(m => m.type === 'SORTIE')
      .reduce((acc, m) => acc + m.quantite, 0);
    this.stats.totalPertes = this.filteredMouvements
      .filter(m => m.type === 'PERDU' || m.type === 'DETRUIT')
      .reduce((acc, m) => acc + m.quantite, 0);
  }

  openModal() {
    this.mouvementForm.reset({ type: 'ENTREE' });
    this.showModal = true;
  }

  saveMouvement() {
    if (this.mouvementForm.invalid) return;
    this.loading = true;
    this.ingredientService.enregistrerMouvement(this.mouvementForm.value).subscribe({
      next: () => {
        this.notification.success('Mouvement enregistré', 'Succès');
        this.showModal = false;
        this.loadData();
        this.loading = false;
      },
      error: () => {
        this.notification.error('Erreur lors de l\'enregistrement', 'Erreur');
        this.loading = false;
      }
    });
  }

  getIngredientName(id: number): string {
    return this.ingredients.find(i => Number(i.id) === Number(id))?.nom || 'Inconnu';
  }
}
