import { Component, EventEmitter, Output, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Fournisseur } from '../../../../core/models/fournisseur.model';
import { FournisseurService } from '../../../../core/services/fournisseur.service';
import { NotificationService } from '../../../../core/services/notification.service';

@Component({
  selector: 'app-fournisseur-form-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './fournisseur-form-modal.component.html',
  styles: [`
    .modal.show { background: rgba(0,0,0,0.5); display: block; }
    .color-marron { color: var(--marron-chocolat); }
    .form-control, .form-select {
      border-radius: 12px;
      padding: 12px 15px;
      border: 1px solid #e5e7eb;
      font-size: 0.95rem;
    }
    .form-control:focus, .form-select:focus {
      border-color: var(--dore-clair);
      box-shadow: 0 0 0 0.25rem rgba(212, 175, 55, 0.1);
    }
    .modal-content {
      border-radius: 24px;
      border: none;
      overflow: hidden;
      box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
    }
    .btn-primary-custom {
      background: linear-gradient(135deg, #8B4513 0%, #5D2E0A 100%);
      color: white;
      border: none;
      border-radius: 12px;
      padding: 12px 25px;
      font-weight: 600;
      transition: all 0.3s ease;
    }
    .btn-primary-custom:hover {
      transform: translateY(-2px);
      box-shadow: 0 8px 15px rgba(139, 69, 19, 0.2);
    }
  `]
})
export class FournisseurFormModalComponent {
  private fb = inject(FormBuilder);
  private supplierService = inject(FournisseurService);
  private notification = inject(NotificationService);

  @Output() supplierSaved = new EventEmitter<void>();

  supplierForm!: FormGroup;
  isOpen = false;
  isEditMode = false;
  loading = false;
  submitted = false;
  currentId?: number;

  constructor() {
    this.initForm();
  }

  initForm() {
    this.supplierForm = this.fb.group({
      nom: ['', [Validators.required, Validators.minLength(2)]],
      telephone: ['', [Validators.required]],
      email: ['', [Validators.email]],
      adresse: ['', Validators.required],
      ville: ['', Validators.required],
      pays: ['Maroc'],
      codePostal: [''],
      notes: [''],
      actif: [true]
    });
  }

  open(s?: Fournisseur) {
    this.isOpen = true;
    this.submitted = false;
    this.isEditMode = !!s;
    this.currentId = s?.id;

    if (s) {
      this.supplierForm.patchValue({
        nom: s.nom,
        telephone: s.telephone,
        email: s.email,
        adresse: s.adresse,
        ville: s.ville,
        pays: s.pays || 'Maroc',
        codePostal: s.codePostal,
        notes: s.notes,
        actif: s.actif
      });
    } else {
      this.supplierForm.reset({
        pays: 'Maroc',
        actif: true
      });
    }
  }

  close() {
    this.isOpen = false;
  }

  get f() { return this.supplierForm.controls; }

  onSubmit() {
    this.submitted = true;
    if (this.supplierForm.invalid) return;

    this.loading = true;
    const data = this.supplierForm.value;

    const request = this.isEditMode 
      ? this.supplierService.update(this.currentId!, data)
      : this.supplierService.create(data);

    request.subscribe({
      next: () => {
        this.loading = false;
        this.notification.success(
          this.isEditMode ? 'Fournisseur mis à jour.' : 'Fournisseur ajouté avec succès.',
          'Succès'
        );
        this.supplierSaved.emit();
        this.close();
      },
      error: (err) => {
        this.loading = false;
        this.notification.error('Une erreur est survenue lors de l\'enregistrement.', 'Erreur');
      }
    });
  }
}
