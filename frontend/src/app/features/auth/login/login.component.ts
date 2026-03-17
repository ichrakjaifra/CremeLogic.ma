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
  styles: [`
    .login-page {
      height: 100vh;
      overflow: hidden;
      background: #FDF4E3;
    }
    .split-container {
      display: flex;
      height: 100%;
    }
    .left-side {
      flex: 1.1;
      background: linear-gradient(135deg, rgba(139, 69, 19, 0.85) 0%, rgba(110, 53, 16, 0.95) 100%), url('https://images.unsplash.com/photo-1555507036-ab1f4038808a?ixlib=rb-1.2.1&auto=format&fit=crop&w=1350&q=80');
      background-size: cover;
      background-position: center;
      padding: 60px;
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      position: relative;
      overflow: hidden;
    }
    .left-side::after {
      content: '';
      position: absolute;
      top: 0; left: 0; right: 0; bottom: 0;
      background: radial-gradient(circle at center, transparent 0%, rgba(0,0,0,0.3) 100%);
    }
    .brand-content {
      position: relative;
      z-index: 2;
      animation: fadeIn 0.8s ease-out;
    }
    .brand-logo {
      max-width: 160px;
      filter: drop-shadow(0 10px 20px rgba(0,0,0,0.3));
    }
    .right-side {
      flex: 1;
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 40px;
      background: #FDF4E3;
    }
    .login-card {
      width: 100%;
      max-width: 480px;
      padding: 50px;
      background: rgba(255, 255, 255, 0.95);
      border-radius: 30px;
      box-shadow: 0 20px 60px rgba(139, 69, 19, 0.12);
      border: 1px solid rgba(255, 255, 255, 0.3);
      animation: slideUp 0.6s ease-out;
    }
    .color-marron {
      color: var(--marron-chocolat);
      font-family: 'Poppins', sans-serif;
    }
    .form-label {
      color: var(--marron-chocolat);
      letter-spacing: 0.5px;
      margin-bottom: 10px;
    }
    .input-group {
      border-radius: 15px;
      overflow: hidden;
      box-shadow: 0 4px 15px rgba(139, 69, 19, 0.05);
      background: white;
      border: 1px solid rgba(139, 69, 19, 0.1);
      transition: all 0.3s ease;
    }
    .input-group:focus-within {
      border-color: var(--dore-clair);
      transform: translateY(-2px);
      box-shadow: 0 8px 25px rgba(139, 69, 19, 0.1);
    }
    .input-group-text {
      background: transparent;
      border: none;
      padding-left: 20px;
      color: var(--marron-chocolat);
    }
    .form-control {
      border: none;
      padding: 14px 15px;
      font-size: 0.95rem;
    }
    .form-control:focus {
      box-shadow: none;
    }
    @keyframes slideUp {
      from { opacity: 0; transform: translateY(30px); }
      to { opacity: 1; transform: translateY(0); }
    }
  `]
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
