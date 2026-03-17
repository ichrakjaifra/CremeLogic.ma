import { Component } from '@angular/core';

@Component({
  selector: 'app-loading-spinner',
  standalone: true,
  template: `
    <div class="spinner-overlay">
      <div class="spinner-container">
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Chargement...</span>
        </div>
        <p class="mt-2 text-white">Un instant s'il vous plaît...</p>
      </div>
    </div>
  `,
  styles: [`
    .spinner-overlay {
      position: fixed;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background: rgba(0, 0, 0, 0.5);
      display: flex;
      justify-content: center;
      align-items: center;
      z-index: 9999;
    }
    .spinner-container {
      text-align: center;
    }
    .spinner-border {
      width: 3rem;
      height: 3rem;
      color: var(--dore-clair) !important;
    }
  `]
})
export class LoadingSpinnerComponent {}
