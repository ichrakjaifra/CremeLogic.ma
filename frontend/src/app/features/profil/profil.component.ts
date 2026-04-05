import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';
import { UserService } from '../../core/services/user.service';
import { ProfileService } from '../../core/services/profile.service';
import { UploadService } from '../../core/services/upload.service';
import { User } from '../../core/models/user.model';
import { NotificationService } from '../../core/services/notification.service';
import { RoleLabelPipe } from '../../shared/pipes/role-label.pipe';

@Component({
  selector: 'app-profil',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RoleLabelPipe],
  templateUrl: './profil.component.html',
  styleUrls: ['./profil.component.css']
})
export class ProfilComponent implements OnInit {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private userService = inject(UserService);
  private profileService = inject(ProfileService);
  private uploadService = inject(UploadService);
  private notification = inject(NotificationService);

  profileForm!: FormGroup;
  passwordForm!: FormGroup;
  currentUser: User | null = null;
  loading = false;
  pwdLoading = false;
  selectedFile: File | null = null;
  showPasswordForm = false;

  activities: any[] = [];

  ngOnInit() {
    this.currentUser = this.authService.getCurrentUser();
    this.initForm();
    this.loadActivities();
  }

  initForm() {
    this.profileForm = this.fb.group({
      prenom: [this.currentUser?.prenom || '', [Validators.required]],
      nom: [this.currentUser?.nom || '', [Validators.required]],
      email: [this.currentUser?.email || '', [Validators.required, Validators.email]],
      telephone: [this.currentUser?.telephone || '', [Validators.required]]
    });

    this.passwordForm = this.fb.group({
      oldPassword: ['', [Validators.required]],
      newPassword: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required]]
    }, { validator: this.passwordMatchValidator });
  }

  passwordMatchValidator(g: FormGroup) {
    return g.get('newPassword')?.value === g.get('confirmPassword')?.value
      ? null : { 'mismatch': true };
  }

  get f() { return this.profileForm.controls; }

  onFileSelected(event: any) {
    const file: File = event.target.files[0];
    if (file) {
      this.selectedFile = file;
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

  updateUser(photoUrl?: string) {
    if (!this.currentUser) return;
    const userData = { ...this.profileForm.value };
    if (photoUrl) userData.photoUrl = photoUrl;

    this.profileService.updateProfile(userData).subscribe({
      next: (updatedUser) => {
        this.notification.success('Profil mis à jour avec succès.', 'Succès');
        this.profileForm.markAsPristine();
        this.loading = false;
        this.selectedFile = null;
        // Update local user info
        this.authService.updateCurrentUser(updatedUser);
        this.currentUser = updatedUser;
      },
      error: () => {
        this.notification.error('Erreur lors de la mise à jour.', 'Erreur');
        this.loading = false;
      }
    });
  }

  changePassword() {
    if (this.passwordForm.invalid) return;

    this.pwdLoading = true;
    this.profileService.changePassword(this.passwordForm.value).subscribe({
      next: () => {
        this.notification.success('Mot de passe changé avec succès.', 'Succès');
        this.passwordForm.reset();
        this.showPasswordForm = false;
        this.pwdLoading = false;
      },
      error: (err) => {
        this.notification.error(err.error?.message || 'Erreur lors du changement de mot de passe.', 'Erreur');
        this.pwdLoading = false;
      }
    });
  }

  loadActivities() {
    this.profileService.getHistorique().subscribe({
      next: (data) => {
        this.activities = data.content || [];
      }
    });
  }

  togglePasswordForm() {
    this.showPasswordForm = !this.showPasswordForm;
  }
}
