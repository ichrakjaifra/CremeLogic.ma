import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';
import { NotificationService } from '../../../core/services/notification.service';
import { TacheService } from '../../../core/services/tache.service';
import { AuthService } from '../../../core/services/auth.service';
import { Tache } from '../../../core/models/tache.model';

@Component({
  selector: 'app-taches',
  standalone: true,
  imports: [CommonModule, FormsModule, EmptyStateComponent],
  templateUrl: './taches.component.html',
  styleUrls: ['./taches.component.css']
})
export class TachesComponent implements OnInit {
  private tacheService = inject(TacheService);
  private authService = inject(AuthService);
  private notification = inject(NotificationService);

  tasks: Tache[] = [];
  filterStatus: 'ALL' | 'PENDING' | 'DONE' = 'ALL';
  loading = false;

  ngOnInit() {
    this.loadTasks();
  }

  loadTasks() {
    const user = this.authService.currentUserValue;
    if (!user) return;

    this.loading = true;
    // For simplicity, showing all tasks for now, or filter by user if role is EMPLOYEE
    const obs = ['ADMIN', 'CHEF'].includes(user.role) 
      ? this.tacheService.getAll() 
      : this.tacheService.getByUtilisateur(user.id);

    obs.subscribe({
      next: (data) => {
        this.tasks = data;
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  get filteredTasks(): Tache[] {
    if (this.filterStatus === 'PENDING') return this.tasks.filter(t => t.statut !== 'TERMINE');
    if (this.filterStatus === 'DONE') return this.tasks.filter(t => t.statut === 'TERMINE');
    return this.tasks;
  }

  get completedTasksCount(): number {
    return this.tasks.filter(t => t.statut === 'TERMINE').length;
  }

  getCount(status: 'PENDING' | 'DONE'): number {
    return this.tasks.filter(t => status === 'DONE' ? t.statut === 'TERMINE' : t.statut !== 'TERMINE').length;
  }

  toggleTask(task: Tache) {
    const newStatut = task.statut === 'TERMINE' ? 'A_FAIRE' : 'TERMINE';
    this.tacheService.updateStatut(task.id, newStatut).subscribe({
      next: (updated) => {
        task.statut = updated.statut;
        if (task.statut === 'TERMINE') {
          this.notification.success(`Tâche "${task.titre}" terminée.`, 'Bravo');
        }
      }
    });
  }
}
