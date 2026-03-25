import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { DashboardService } from '../../../core/services/dashboard.service';
import { EmployeStats } from '../../../core/models/dashboard.model';
import { VenteService } from '../../../core/services/vente.service';
import { Vente } from '../../../core/models/vente.model';
import { FormatPricePipe } from '../../../shared/pipes/format-price.pipe';
import { NotificationService } from '../../../core/services/notification.service';
import { AuthService } from '../../../core/services/auth.service';
import { OrdreProductionService } from '../../../core/services/ordre-production.service';
import { OrdreProduction } from '../../../core/models/ordre-production.model';

@Component({
  selector: 'app-employe-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, FormatPricePipe],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class EmployeDashboardComponent implements OnInit {
  private dashboardService = inject(DashboardService);
  private venteService = inject(VenteService);
  private authService = inject(AuthService);
  private notification = inject(NotificationService);
  private productionService = inject(OrdreProductionService);

  stats?: EmployeStats;
  derniereVente?: Vente;
  productionTasks: OrdreProduction[] = [];
  today = new Date();
  employeeName: string = 'Employé';
  heureArrivee: string = '--:--';

  ngOnInit() {
    this.employeeName = this.authService.getCurrentUser()?.nom || 'Employé';
    this.trackHeureArrivee();

    this.dashboardService.getEmployeStats().subscribe(stats => {
      this.stats = stats;
    });

    this.venteService.getAll().subscribe(ventes => {
      if (ventes && Array.isArray(ventes) && ventes.length > 0) {
        this.derniereVente = ventes[0];
      }
    });

    this.productionService.getAll().subscribe(tasks => {
        // Filter for today's tasks and for this employee (if assigned)
        const user = this.authService.getCurrentUser();
        this.productionTasks = tasks.filter(t => {
            const isToday = new Date(t.dateDebutPrevue).toDateString() === new Date().toDateString();
            const isAssigned = !t.responsableNom || t.responsableNom === user?.nom; 
            return isToday && isAssigned && t.statut !== 'TERMINEE' && t.statut !== 'ANNULEE';
        });
    });
  }

  private trackHeureArrivee() {
    const todayKey = 'arrival_' + new Date().toISOString().split('T')[0];
    let savedTime = localStorage.getItem(todayKey);
    
    if (!savedTime) {
      savedTime = new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
      localStorage.setItem(todayKey, savedTime);
    }
    this.heureArrivee = savedTime;
  }

  getVenteDisplayId(vente: Vente | undefined): string | number {
    if (!vente) return '';
    if (vente.numeroVente && vente.numeroVente.includes('-')) {
      return vente.numeroVente.split('-')[1];
    }
    return vente.id;
  }
}
