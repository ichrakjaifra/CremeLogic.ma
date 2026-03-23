import { Component, EventEmitter, Output, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Ingredient } from '../../../../core/models/ingredient.model';
import { IngredientService } from '../../../../core/services/ingredient.service';
import { FournisseurService } from '../../../../core/services/fournisseur.service';
import { NotificationService } from '../../../../core/services/notification.service';
import { Fournisseur } from '../../../../core/models/commande-achat.model';

@Component({
  selector: 'app-ingredient-form-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './ingredient-form-modal.component.html',
  styles: [`
    .modal.show { background: rgba(0,0,0,0.5); }
    .color-marron { color: var(--marron-chocolat); }
    .form-control, .form-select {
      border-radius: 10px;
      padding: 12px;
      border: 1px solid #e5e7eb;
    }
    .form-control:focus, .form-select:focus {
      border-color: var(--dore-clair);
      box-shadow: 0 0 0 0.2rem rgba(212, 175, 55, 0.1);
    }
  `]
})
export class IngredientFormModalComponent implements OnInit {
  private fb = inject(FormBuilder);
  private ingredientService = inject(IngredientService);
  private fournisseurService = inject(FournisseurService);
  private notification = inject(NotificationService);

  @Output() ingredientSaved = new EventEmitter<void>();

  ingredientForm!: FormGroup;
  isOpen = false;
  isEditMode = false;
  loading = false;
  submitted = false;
  currentIngredientId?: number;
  fournisseurs: Fournisseur[] = [];

  unites = ['GRAMME', 'KILOGRAMME', 'LITRE', 'MILLILITRE', 'UNITE', 'PIECE', 'CARTON'];

  constructor() {
    this.initForm();
  }

  ngOnInit() {
    this.loadFournisseurs();
  }

  initForm() {
    this.ingredientForm = this.fb.group({
      nom: ['', Validators.required],
      codeIngredient: [''], // Usually auto-generated but can be provided
      description: [''],
      uniteMesure: ['KILOGRAMME', Validators.required],
      prixUnitaire: [0, [Validators.required, Validators.min(0)]],
      quantiteMinimum: [0, [Validators.required, Validators.min(0)]],
      quantiteMaximum: [1000, [Validators.required, Validators.min(0)]],
      perissable: [false],
      dateExpiration: [null],
      fournisseurPrincipalId: [null]
    });
  }

  loadFournisseurs() {
    this.fournisseurService.getAll().subscribe(data => {
      this.fournisseurs = data;
    });
  }

  open(ingredient?: Ingredient) {
    this.isOpen = true;
    this.submitted = false;
    this.isEditMode = !!ingredient;
    this.currentIngredientId = ingredient?.id;

    if (ingredient) {
      this.ingredientForm.patchValue({
        nom: ingredient.nom,
        codeIngredient: ingredient.codeIngredient,
        description: ingredient.description,
        uniteMesure: ingredient.uniteMesure,
        prixUnitaire: ingredient.prixUnitaire,
        quantiteMinimum: ingredient.quantiteMinimum,
        quantiteMaximum: ingredient.quantiteMaximum,
        perissable: ingredient.perissable,
        dateExpiration: ingredient.dateExpiration ? new Date(ingredient.dateExpiration).toISOString().substring(0, 10) : null,
        fournisseurPrincipalId: ingredient.fournisseurId || (ingredient as any).fournisseurPrincipalId
      });
    } else {
      this.ingredientForm.reset({
        uniteMesure: 'KILOGRAMME',
        prixUnitaire: 0,
        quantiteMinimum: 0,
        quantiteMaximum: 1000,
        perissable: false
      });
    }
  }

  close() {
    this.isOpen = false;
  }

  get f() { return this.ingredientForm.controls; }

  onSubmit() {
    this.submitted = true;
    if (this.ingredientForm.invalid) return;

    this.loading = true;
    const ingredientData = this.ingredientForm.value;

    const request = this.isEditMode 
      ? this.ingredientService.update(this.currentIngredientId!, ingredientData)
      : this.ingredientService.create(ingredientData);

    request.subscribe({
      next: () => {
        this.loading = false;
        this.notification.success(
          this.isEditMode ? 'Ingrédient mis à jour.' : 'Ingrédient créé avec succès.',
          'Succès'
        );
        this.ingredientSaved.emit();
        this.close();
      },
      error: (err) => {
        this.loading = false;
        console.error('Error saving ingredient:', err);
      }
    });
  }
}
