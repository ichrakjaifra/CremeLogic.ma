import { Component, EventEmitter, Output, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Ingredient } from '../../../../core/models/ingredient.model';
import { IngredientService } from '../../../../core/services/ingredient.service';
import { NotificationService } from '../../../../core/services/notification.service';

@Component({
  selector: 'app-stock-mouvement-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './stock-mouvement-modal.component.html',
  styles: [`
    .modal.show { background: rgba(0,0,0,0.5); }
    .color-marron { color: var(--marron-chocolat); }
    .form-control, .form-select {
      border-radius: 10px;
      padding: 12px;
      border: 1px solid #e5e7eb;
    }
  `]
})
export class StockMouvementModalComponent {
  private fb = inject(FormBuilder);
  private ingredientService = inject(IngredientService);
  private notification = inject(NotificationService);

  @Output() mouvementSaved = new EventEmitter<void>();

  mouvementForm!: FormGroup;
  isOpen = false;
  loading = false;
  submitted = false;
  ingredients: Ingredient[] = [];

  constructor() {
    this.initForm();
  }

  initForm() {
    this.mouvementForm = this.fb.group({
      ingredientId: [null, Validators.required],
      type: ['ENTREE', Validators.required],
      quantite: [0, [Validators.required, Validators.min(0.01)]],
      raison: ['']
    });
  }

  open(ingredients: Ingredient[], selectedIngredientId?: number) {
    this.isOpen = true;
    this.submitted = false;
    this.ingredients = ingredients;
    this.mouvementForm.reset({
      ingredientId: selectedIngredientId || null,
      type: 'ENTREE',
      quantite: 0,
      raison: ''
    });
  }

  close() {
    this.isOpen = false;
  }

  get f() { return this.mouvementForm.controls; }

  onSubmit() {
    this.submitted = true;
    if (this.mouvementForm.invalid) return;

    this.loading = true;
    const { ingredientId, quantite, type, raison } = this.mouvementForm.value;

    this.ingredientService.ajusterStock(ingredientId, quantite, type, raison).subscribe({
      next: () => {
        this.loading = false;
        this.notification.success('Mouvement de stock enregistré.', 'Succès');
        this.mouvementSaved.emit();
        this.close();
      },
      error: (err) => {
        this.loading = false;
        console.error('Error recording movement:', err);
      }
    });
  }
}
