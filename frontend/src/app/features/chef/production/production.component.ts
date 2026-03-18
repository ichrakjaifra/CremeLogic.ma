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
  styleUrls: ['./production.component.css']
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
