import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { OrdreProductionService } from '../../../core/services/ordre-production.service';
import { AuthService } from '../../../core/services/auth.service';
import { OrdreProduction } from '../../../core/models/ordre-production.model';
import { NotificationService } from '../../../core/services/notification.service';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';
import { ConfirmDirective } from '../../../shared/directives/confirm.directive';
import { NgxPaginationModule } from 'ngx-pagination';

@Component({
  selector: 'app-production',
  standalone: true,
  imports: [CommonModule, FormsModule, EmptyStateComponent, ConfirmDirective, NgxPaginationModule],
  templateUrl: './production.component.html',
  styles: [`
    .production-page {
      min-height: 100vh;
      animation: fadeIn 0.5s ease-out;
    }
    .order-card {
      transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
      border: 1px solid rgba(255, 255, 255, 0.3);
      border-left: 5px solid transparent;
    }
    .order-card:hover { transform: translateX(5px); box-shadow: 0 10px 20px rgba(139, 69, 19, 0.05) !important; }
    .status-planifie { border-left-color: #95a5a6; }
    .status-en_cours { border-left-color: #f1c40f; }
    .status-termine { border-left-color: #2ecc71; }
    .status-annule { border-left-color: #e74c3c; }
    
    .status-badge { font-size: 0.7rem; letter-spacing: 0.5px; }
    .bg-chocolate-light { background: #FDF4E3; }
    
    .btn-status {
      padding: 8px 16px;
      border-radius: 10px;
      font-size: 0.8rem;
      font-weight: 600;
      transition: all 0.2s;
    }
  `]
})
export class ProductionComponent implements OnInit {
  private ordreService = inject(OrdreProductionService);
  private authService = inject(AuthService);
  private notification = inject(NotificationService);

  ordres: OrdreProduction[] = [];
  filteredOrdres: OrdreProduction[] = [];
  filterStatut: string = 'ALL';
  p: number = 1;
  loading = false;
  userRole: string | null = null;

  ngOnInit() {
    this.userRole = this.authService.getRole();
    this.loadOrdres();
  }

  canEdit(): boolean {
    return ['ADMIN', 'CHEF'].includes(this.userRole || '');
  }

  loadOrdres() {
    this.loading = true;
    this.ordreService.getAll().subscribe({
      next: (data) => {
        this.ordres = data;
        this.applyFilters();
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  applyFilters() {
    if (this.filterStatut === 'ALL') {
      this.filteredOrdres = this.ordres;
    } else {
      this.filteredOrdres = this.ordres.filter(o => o.statut === this.filterStatut);
    }
    this.p = 1;
  }

  demarrerOrdre(id: number) {
    this.ordreService.demarrer(id).subscribe({
      next: () => {
        this.notification.success('Production démarrée.', 'Chef');
        this.loadOrdres();
      }
    });
  }

  terminerOrdre(id: number) {
    this.ordreService.terminer(id).subscribe({
      next: () => {
        this.notification.success('Production terminée avec succès.', 'Félicitations');
        this.loadOrdres();
      }
    });
  }

  annulerOrdre(id: number) {
    this.ordreService.annuler(id, 'Annulation par le chef').subscribe({
      next: () => {
        this.notification.warning('Production annulée.', 'Avertissement');
        this.loadOrdres();
      }
    });
  }

  getStatutLabel(statut: string): string {
    switch (statut) {
      case 'PLANIFIE': return 'PLANIFIÉ';
      case 'EN_COURS': return 'EN COURS';
      case 'TERMINE': return 'TERMINÉ';
      case 'ANNULE': return 'ANNULÉ';
      default: return statut;
    }
  }

  getStatutClass(statut: string): string {
    return 'status-' + statut.toLowerCase();
  }
}
