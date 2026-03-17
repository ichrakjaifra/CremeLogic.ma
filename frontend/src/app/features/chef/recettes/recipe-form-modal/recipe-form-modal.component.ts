import { Component, EventEmitter, Output, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormArray, ReactiveFormsModule, Validators } from '@angular/forms';
import { Recette, LigneRecette } from '../../../../core/models/recette.model';
import { RecetteService } from '../../../../core/services/recette.service';
import { IngredientService } from '../../../../core/services/ingredient.service';
import { Ingredient } from '../../../../core/models/ingredient.model';
import { NotificationService } from '../../../../core/services/notification.service';
import { FormatPricePipe } from '../../../../shared/pipes/format-price.pipe';

@Component({
  selector: 'app-recipe-form-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormatPricePipe],
  templateUrl: './recipe-form-modal.component.html',
  styles: [`
    .modal.show { background: rgba(0,0,0,0.5); backdrop-filter: blur(4px); }
    .glass-modal { background: rgba(255, 255, 255, 0.95); border-radius: 25px; border: 1px solid rgba(255, 255, 255, 0.3); }
    .color-marron { color: var(--marron-chocolat); }
    .bg-chocolate-light { background: #FDF4E3; }
    .ligne-item { background: #fbfbfb; border-radius: 12px; transition: all 0.2s; }
    .ligne-item:hover { background: #f3f4f6; }
    .form-floating > .form-control, .form-floating > .form-select { height: calc(3rem + 2px); line-height: 1.25; }
    .form-floating > label { padding: 0.75rem 0.75rem; }
  `]
})
export class RecipeFormModalComponent implements OnInit {
  private fb = inject(FormBuilder);
  private recetteService = inject(RecetteService);
  private ingredientService = inject(IngredientService);
  private notification = inject(NotificationService);

  @Output() recipeSaved = new EventEmitter<void>();

  recipeForm!: FormGroup;
  ingredients: Ingredient[] = [];
  isOpen = false;
  isEditMode = false;
  loading = false;
  submitted = false;
  currentRecipeId?: number;

  ngOnInit() {
    this.initForm();
    this.loadIngredients();
  }

  initForm() {
    this.recipeForm = this.fb.group({
      nom: ['', Validators.required],
      description: [''],
      instructions: [''],
      tempsPreparation: [30, [Validators.required, Validators.min(0)]],
      tempsCuisson: [0, [Validators.required, Validators.min(0)]],
      nombrePortions: [1, [Validators.required, Validators.min(1)]],
      lignesRecette: this.fb.array([])
    });
  }

  loadIngredients() {
    this.ingredientService.getAll().subscribe(data => this.ingredients = data);
  }

  get lignesRecette() {
    return this.recipeForm.get('lignesRecette') as FormArray;
  }

  addLigne(ligne?: LigneRecette) {
    const ligneForm = this.fb.group({
      ingredientId: [ligne?.ingredientId || '', Validators.required],
      quantite: [ligne?.quantite || 1, [Validators.required, Validators.min(0.01)]]
    });
    this.lignesRecette.push(ligneForm);
  }

  removeLigne(index: number) {
    this.lignesRecette.removeAt(index);
  }

  open(recette?: Recette) {
    this.isOpen = true;
    this.submitted = false;
    this.isEditMode = !!recette;
    this.currentRecipeId = recette?.id;
    this.lignesRecette.clear();

    if (recette) {
      this.recipeForm.patchValue({
        nom: recette.nom,
        description: recette.description,
        instructions: recette.instructions,
        tempsPreparation: recette.tempsPreparation,
        tempsCuisson: recette.tempsCuisson,
        nombrePortions: recette.nombrePortions
      });
      recette.lignesRecette.forEach(l => this.addLigne(l));
    } else {
      this.recipeForm.reset({ tempsPreparation: 30, tempsCuisson: 0, nombrePortions: 1 });
      this.addLigne(); // Add one empty line by default
    }
  }

  close() {
    this.isOpen = false;
  }

  onSubmit() {
    this.submitted = true;
    if (this.recipeForm.invalid) {
      this.notification.warning('Veuillez remplir tous les champs obligatoires.', 'Formulaire invalide');
      return;
    }

    this.loading = true;
    const recipeData = this.recipeForm.value;

    const request = this.isEditMode 
      ? this.recetteService.update(this.currentRecipeId!, recipeData)
      : this.recetteService.create(recipeData);

    request.subscribe({
      next: () => {
        this.loading = false;
        this.notification.success(
          this.isEditMode ? 'Recette mise à jour.' : 'Recette créée avec succès.',
          'Succès'
        );
        this.recipeSaved.emit();
        this.close();
      },
      error: () => this.loading = false
    });
  }

  getIngredientPrix(id: any): number {
    const ing = this.ingredients.find(i => i.id == id);
    return ing ? ing.prixUnitaire : 0;
  }

  getIngredientUnite(id: any): string {
    const ing = this.ingredients.find(i => i.id == id);
    return ing ? ing.uniteMesure : '';
  }

  get calculateTotalCost(): number {
    let total = 0;
    this.lignesRecette.controls.forEach(ctrl => {
      const id = ctrl.get('ingredientId')?.value;
      const qty = ctrl.get('quantite')?.value || 0;
      if (id) {
        total += this.getIngredientPrix(id) * qty;
      }
    });
    return total;
  }
}
