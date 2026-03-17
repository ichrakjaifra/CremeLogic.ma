import { Component, EventEmitter, Output, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { User } from '../../../../core/models/user.model';
import { UserService } from '../../../../core/services/user.service';
import { NotificationService } from '../../../../core/services/notification.service';

@Component({
  selector: 'app-user-form-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './user-form-modal.component.html',
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
export class UserFormModalComponent {
  private fb = inject(FormBuilder);
  private userService = inject(UserService);
  private notification = inject(NotificationService);

  @Output() userSaved = new EventEmitter<void>();

  userForm!: FormGroup;
  isOpen = false;
  isEditMode = false;
  loading = false;
  submitted = false;
  currentUserId?: number;

  constructor() {
    this.initForm();
  }

  initForm() {
    this.userForm = this.fb.group({
      prenom: ['', Validators.required],
      nom: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      telephone: [''],
      role: ['EMPLOYE', Validators.required],
      actif: [true],
      password: ['', this.isEditMode ? [] : [Validators.required, Validators.minLength(6)]]
    });
  }

  open(user?: User) {
    this.isOpen = true;
    this.submitted = false;
    this.isEditMode = !!user;
    this.currentUserId = user?.id;

    if (user) {
      this.userForm.patchValue({
        prenom: user.prenom,
        nom: user.nom,
        email: user.email,
        telephone: user.telephone,
        role: user.role,
        actif: user.actif
      });
      this.userForm.get('password')?.clearValidators();
    } else {
      this.userForm.reset({ role: 'EMPLOYE', actif: true });
      this.userForm.get('password')?.setValidators([Validators.required, Validators.minLength(6)]);
    }
    this.userForm.get('password')?.updateValueAndValidity();
  }

  close() {
    this.isOpen = false;
  }

  get f() { return this.userForm.controls; }

  onSubmit() {
    this.submitted = true;
    if (this.userForm.invalid) return;

    this.loading = true;
    const userData = this.userForm.value;

    const request = this.isEditMode 
      ? this.userService.update(this.currentUserId!, userData)
      : this.userService.create(userData);

    request.subscribe({
      next: () => {
        this.loading = false;
        this.notification.success(
          this.isEditMode ? 'Utilisateur mis à jour.' : 'Utilisateur créé avec succès.',
          'Succès'
        );
        this.userSaved.emit();
        this.close();
      },
      error: () => this.loading = false
    });
  }
}
