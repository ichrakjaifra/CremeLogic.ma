import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { DashboardService } from '../../../core/services/dashboard.service';
import { EmployeStats } from '../../../core/models/dashboard.model';
import { VenteService } from '../../../core/services/vente.service';
import { Vente } from '../../../core/models/vente.model';
import { FormatPricePipe } from '../../../shared/pipes/format-price.pipe';
import { NotificationService } from '../../../core/services/notification.service';

@Component({
  selector: 'app-employe-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, FormatPricePipe],
  templateUrl: './dashboard.component.html',
  styles: [`
    .employe-dashboard { background-color: var(--bg-global); min-height: 100vh; }
    .kpi-icon { width: 50px; height: 50px; border-radius: 12px; display: flex; align-items: center; justify-content: center; font-size: 1.5rem; }
    .bg-success-soft { background-color: #f0fdf4; }
    .bg-warning-soft { background-color: #fffbeb; }
    .bg-info-soft { background-color: #eff6ff; }
    .bg-beige-light { background-color: #fff9f0; }
    .extra-small { font-size: 0.75rem; }
    .task-item { transition: background-color 0.2s; }
    .task-item:hover { background-color: #f8f9fa !important; }
  `]
})
export class EmployeDashboardComponent implements OnInit {
  private dashboardService = inject(DashboardService);
  private venteService = inject(VenteService);
  private notification = inject(NotificationService);

  stats?: EmployeStats;
  derniereVente?: Vente;
  today = new Date();
  
  mockTasks = [
    { id: 1, title: 'Préparer 20 baguettes de tradition', deadline: 'Avant 11:00', category: 'Production' },
    { id: 2, title: 'Vérifier la vitrine pâtisserie', deadline: 'Dès que possible', category: 'Hygiène' },
    { id: 3, title: 'Nettoyer le four n°2', deadline: 'Fin de service', category: 'Maintenance' },
    { id: 4, title: 'Réceptionner commande farine', deadline: 'Vers 14:00', category: 'Stock' }
  ];

  ngOnInit() {
    this.dashboardService.getEmployeStats().subscribe(stats => {
      this.stats = stats;
    });

    this.venteService.getAll().subscribe(ventes => {
      if (ventes.length > 0) {
        this.derniereVente = ventes[0];
      }
    });
  }

  completeTask(task: any) {
    this.notification.success(`Tâche "${task.title}" terminée !`, 'Bravo');
    this.mockTasks = this.mockTasks.filter(t => t.id !== task.id);
  }
}
