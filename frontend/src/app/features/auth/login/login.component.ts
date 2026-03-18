import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { NotificationService } from '../../../core/services/notification.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);
  private notification = inject(NotificationService);

  loginForm!: FormGroup;
  loading = false;
  submitted = false;
  showPassword = false;

  ngOnInit() {
    // If already logged in, redirect
    if (this.authService.isLoggedIn()) {
      this.redirectByRole();
    }

    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  get f() { return this.loginForm.controls; }

  togglePassword() {
    this.showPassword = !this.showPassword;
  }

  onSubmit() {
    this.submitted = true;

    if (this.loginForm.invalid) {
      return;
    }

    this.loading = true;
    const { email, password } = this.loginForm.value;

    this.authService.login({ email, password }).subscribe({
      next: (user) => {
        this.notification.success(`Bienvenue ${user.prenom} !`, 'Connexion réussie');
        this.redirectByRole();
      },
      error: (err) => {
        this.loading = false;
        // The error interceptor already handles the notification, 
        // but we might want custom logic here if needed.
      }
    });
  }

  private redirectByRole() {
    const role = this.authService.getRole();
    switch (role) {
      case 'ADMIN':
        this.router.navigate(['/admin/dashboard']);
        break;
      case 'CHEF':
        this.router.navigate(['/chef/dashboard']);
        break;
      case 'MAGASINIER':
        this.router.navigate(['/magasinier/dashboard']);
        break;
      case 'EMPLOYE':
        this.router.navigate(['/employe/dashboard']);
        break;
      default:
        this.router.navigate(['/profil']);
    }
  }
}
