import { Component, EventEmitter, Output, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Fournisseur } from '../../../../core/models/fournisseur.model';
import { FournisseurService } from '../../../../core/services/fournisseur.service';
import { NotificationService } from '../../../../core/services/notification.service';

@Component({
  selector: 'app-fournisseur-evaluation-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './fournisseur-evaluation-modal.component.html',
  styles: [`
    .modal.show { background: rgba(0,0,0,0.5); display: block; }
    .star-rating {
      font-size: 2.2rem;
      cursor: pointer;
      color: #e0e0e0;
      transition: all 0.2s ease;
    }
    .star-rating.selected { color: #f1c40f; }
    .star-rating:hover { transform: scale(1.1); }
    .color-marron { color: var(--marron-chocolat); }
    .modal-content { border-radius: 20px; border: none; }
  `]
})
export class FournisseurEvaluationModalComponent {
  private supplierService = inject(FournisseurService);
  private notification = inject(NotificationService);

  @Output() evaluationSaved = new EventEmitter<void>();

  isOpen = false;
  loading = false;
  supplier?: Fournisseur;
  note = 0;
  commentaire = '';
  hoveredNote = 0;

  open(s: Fournisseur) {
    this.supplier = s;
    this.note = Math.round(s.noteEvaluation || 0);
    this.commentaire = '';
    this.isOpen = true;
  }

  close() {
    this.isOpen = false;
  }

  setNote(n: number) {
    this.note = n;
  }

  onSubmit() {
    if (this.note === 0) {
      this.notification.warning('Veuillez sélectionner une note.', 'Attention');
      return;
    }

    this.loading = true;
    this.supplierService.evaluer(this.supplier!.id, this.note, this.commentaire).subscribe({
      next: () => {
        this.loading = false;
        this.notification.success('Évaluation enregistrée avec succès.', 'Succès');
        this.evaluationSaved.emit();
        this.close();
      },
      error: () => {
        this.loading = false;
        this.notification.error('Erreur lors de l\'enregistrement de l\'évaluation.', 'Erreur');
      }
    });
  }
}
