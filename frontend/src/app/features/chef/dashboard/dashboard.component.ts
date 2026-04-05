import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { DashboardService } from '../../../core/services/dashboard.service';
import { ChefStats } from '../../../core/models/dashboard.model';
import { OrdreProductionService } from '../../../core/services/ordre-production.service';
import { TacheService } from '../../../core/services/tache.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-chef-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class ChefDashboardComponent implements OnInit {
  private dashboardService = inject(DashboardService);
  private tacheService = inject(TacheService);
  private authService = inject(AuthService);

  stats?: ChefStats;
  ordresRecents: any[] = [];
  today = new Date();

  ngOnInit() {
    this.loadStats();
  }

  loadStats() {
    this.dashboardService.getChefStats().subscribe(stats => {
      this.stats = stats;
      if (stats && stats.productionsRecent) {
        this.ordresRecents = stats.productionsRecent;
      }
    });
  }

  updateTaskStatut(taskId: number, newStatut: string): void {
    this.tacheService.updateStatut(taskId, newStatut).subscribe({
      next: () => this.loadStats(),
      error: (err) => console.error('Erreur lors de la mise à jour de la tâche', err)
    });
  }

  openTaskModal(): void {
    const titre = prompt('Titre de la tâche :');
    if (titre) {
      const currentUser = this.authService.currentUserValue;
      if (!currentUser) return;

      this.tacheService.create({
        titre: titre,
        description: 'Tâche créée depuis le dashboard',
        priorite: 'MOYENNE',
        statut: 'A_FAIRE',
        dateEcheance: new Date(),
        assigneAId: currentUser.id
      }).subscribe({
        next: () => this.loadStats(),
        error: (err) => console.error('Erreur lors de la création de la tâche', err)
      });
    }
  }
}
