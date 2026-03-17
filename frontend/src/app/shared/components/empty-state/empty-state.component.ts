import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-empty-state',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="empty-state">
      <div class="icon-container">
        <i [class]="icon"></i>
      </div>
      <h3>{{ title }}</h3>
      <p>{{ description }}</p>
      <button *ngIf="actionText" (click)="action.emit()" class="btn-primary-custom mt-3">
        {{ actionText }}
      </button>
    </div>
  `,
  styles: [`
    .empty-state {
      padding: 40px;
      text-align: center;
      background: #fff;
      border-radius: 12px;
      margin: 20px 0;
    }
    .icon-container {
      font-size: 4rem;
      color: #ddd;
      margin-bottom: 20px;
    }
    h3 {
      font-weight: 600;
      color: var(--marron-chocolat);
    }
    p {
      color: #777;
    }
  `]
})
export class EmptyStateComponent {
  @Input() title: string = 'Aucune donnée trouvée';
  @Input() description: string = 'Il n\'y a rien à afficher ici pour le moment.';
  @Input() icon: string = 'fas fa-box-open';
  @Input() actionText?: string;
  @Output() action = new EventEmitter<void>();
}
