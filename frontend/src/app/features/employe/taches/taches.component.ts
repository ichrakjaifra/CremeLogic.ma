import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';
import { NotificationService } from '../../../core/services/notification.service';

interface Task {
  id: number;
  title: string;
  category: string;
  deadline: string;
  done: boolean;
}

@Component({
  selector: 'app-taches',
  standalone: true,
  imports: [CommonModule, FormsModule, EmptyStateComponent],
  templateUrl: './taches.component.html',
  styles: [`
    .tasks-page {
      min-height: 100vh;
      animation: fadeIn 0.5s ease-out;
    }
    .task-card {
      transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
      border: 1px solid rgba(255, 255, 255, 0.4);
      cursor: pointer;
    }
    .task-card:hover { border-color: var(--dore-clair); transform: translateX(5px); }
    .task-card.completed { opacity: 0.6; background: rgba(255, 255, 255, 0.2); }
    .task-card.completed .task-title { text-decoration: line-through; }
    
    .category-tag { font-size: 0.65rem; font-weight: 800; letter-spacing: 0.5px; }
    .bg-chocolate-light { background: #FDF4E3; }
    
    .custom-checkbox {
      width: 24px;
      height: 24px;
      border: 2px solid var(--marron-chocolat);
      border-radius: 6px;
      cursor: pointer;
      display: flex;
      align-items: center;
      justify-content: center;
      transition: all 0.2s;
    }
    .custom-checkbox.checked { background: var(--marron-chocolat); color: white; }
    
    .progress-header {
      background: rgba(255, 255, 255, 0.5);
      backdrop-filter: blur(10px);
      border-radius: 25px;
      border: 1px solid rgba(255, 255, 255, 0.5);
    }
  `]
})
export class TachesComponent implements OnInit {
  private notification = inject(NotificationService);

  tasks: Task[] = [
    { id: 1, title: 'Préparer 20 baguettes de tradition', deadline: 'Aujourd\'hui 11:00', category: 'Production', done: false },
    { id: 2, title: 'Vérifier la vitrine pâtisserie', deadline: 'Aujourd\'hui 09:00', category: 'Hygiène', done: true },
    { id: 3, title: 'Nettoyer le four n°2', deadline: 'Aujourd\'hui 17:00', category: 'Maintenance', done: false },
    { id: 4, title: 'Réceptionner commande farine', deadline: 'Aujourd\'hui 14:00', category: 'Stock', done: false },
    { id: 5, title: 'Inventaire des boîtes d\'emballage', deadline: 'Demain 10:00', category: 'Stock', done: false }
  ];

  filterStatus: 'ALL' | 'PENDING' | 'DONE' = 'ALL';

  ngOnInit() {}

  get filteredTasks(): Task[] {
    if (this.filterStatus === 'PENDING') return this.tasks.filter(t => !t.done);
    if (this.filterStatus === 'DONE') return this.tasks.filter(t => t.done);
    return this.tasks;
  }

  get completedTasksCount(): number {
    return this.tasks.filter(t => t.done).length;
  }

  getCount(status: 'PENDING' | 'DONE'): number {
    return this.tasks.filter(t => status === 'DONE' ? t.done : !t.done).length;
  }

  toggleTask(task: Task) {
    task.done = !task.done;
    if (task.done) {
      this.notification.success(`Tâche "${task.title}" terminée.`, 'Bravo');
    }
  }
}
