import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';
import { UserService } from '../../core/services/user.service';
import { UploadService } from '../../core/services/upload.service';
import { User } from '../../core/models/user.model';
import { NotificationService } from '../../core/services/notification.service';

@Component({
  selector: 'app-profil',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './profil.component.html',
  styles: [`
    .profile-page {
      min-height: 100vh;
      animation: fadeIn 0.5s ease-out;
    }
    .profile-card {
      border: 1px solid rgba(255, 255, 255, 0.4);
      border-radius: 30px;
      overflow: hidden;
    }
    .avatar-wrapper {
      position: relative;
      width: 120px;
      height: 120px;
      margin: 0 auto;
    }
    .profile-avatar {
      width: 100%;
      height: 100%;
      border-radius: 50%;
      object-fit: cover;
      border: 4px solid white;
      box-shadow: 0 10px 25px rgba(139, 69, 19, 0.1);
    }
    .edit-avatar-btn {
      position: absolute;
      bottom: 5px;
      right: 5px;
      width: 32px;
      height: 32px;
      border-radius: 50%;
      background: var(--marron-chocolat);
      color: white;
      border: 2px solid white;
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
    }
    .history-item {
      position: relative;
      padding-left: 25px;
      padding-bottom: 20px;
      border-left: 2px solid rgba(139, 69, 19, 0.1);
    }
    .history-item::before {
      content: '';
      position: absolute;
      left: -7px;
      top: 0;
      width: 12px;
      height: 12px;
      border-radius: 50%;
      background: var(--dore-clair);
      border: 2px solid white;
    }
    .bg-chocolate-light { background: #FDF4E3; }
  `]
})
export class ProfilComponent implements OnInit {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private userService = inject(UserService);
  private uploadService = inject(UploadService);
  private notification = inject(NotificationService);

  profileForm!: FormGroup;
  currentUser: User | null = null;
  loading = false;
  selectedFile: File | null = null;

  activities = [
    { type: 'LOGIN', description: 'Connexion réussie', date: new Date(), icon: 'fas fa-sign-in-alt' },
    { type: 'UPDATE', description: 'Mise à jour du profil', date: new Date(Date.now() - 86400000), icon: 'fas fa-user-edit' },
    { type: 'SALE', description: 'Vente #2045 effectuée', date: new Date(Date.now() - 172800000), icon: 'fas fa-shopping-cart' }
  ];

  ngOnInit() {
    this.currentUser = this.authService.getCurrentUser();
    this.initForm();
  }

  initForm() {
    this.profileForm = this.fb.group({
      prenom: [this.currentUser?.prenom || '', [Validators.required]],
      nom: [this.currentUser?.nom || '', [Validators.required]],
      email: [this.currentUser?.email || '', [Validators.required, Validators.email]],
      telephone: [this.currentUser?.telephone || '', [Validators.required]]
    });
  }

  get f() { return this.profileForm.controls; }

  onFileSelected(event: any) {
    const file: File = event.target.files[0];
    if (file) {
      this.selectedFile = file;
      // Optionnel: Prévisualisation locale
      const reader = new FileReader();
      reader.onload = (e: any) => {
        if (this.currentUser) this.currentUser.photoUrl = e.target.result;
      };
      reader.readAsDataURL(file);
    }
  }

  saveProfile() {
    if (this.profileForm.invalid || !this.currentUser) return;

    this.loading = true;
    if (this.selectedFile) {
      this.uploadService.uploadImage(this.selectedFile).subscribe({
        next: (response) => {
          this.updateUser(response.url);
        },
        error: () => {
          this.notification.error('Erreur lors du chargement de l\'image.', 'Erreur');
          this.loading = false;
        }
      });
    } else {
      this.updateUser();
    }
  }

  private updateUser(photoUrl?: string) {
    if (!this.currentUser) return;
    const userData = { ...this.profileForm.value };
    if (photoUrl) userData.photoUrl = photoUrl;

    this.userService.update(this.currentUser.id, userData).subscribe({
      next: (updatedUser) => {
        this.notification.success('Profil mis à jour avec succès.', 'Succès');
        this.profileForm.markAsPristine();
        this.loading = false;
        this.selectedFile = null;
      },
      error: () => {
        this.notification.error('Erreur lors de la mise à jour.', 'Erreur');
        this.loading = false;
      }
    });
  }

  openChangePassword() {
     this.notification.info('La fonction de changement de mot de passe sera disponible prochainement.', 'Info');
  }
}
