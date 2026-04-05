import { Component, OnInit, inject, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../../core/services/user.service';
import { User } from '../../../core/models/user.model';
import { RoleLabelPipe } from '../../../shared/pipes/role-label.pipe';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';
import { ConfirmDirective } from '../../../shared/directives/confirm.directive';
import { NgxPaginationModule } from 'ngx-pagination';
import { NotificationService } from '../../../core/services/notification.service';
import { UserFormModalComponent } from './user-form-modal/user-form-modal.component';

@Component({
  selector: 'app-utilisateurs',
  standalone: true,
  imports: [
    CommonModule, 
    FormsModule, 
    RoleLabelPipe, 
    EmptyStateComponent, 
    ConfirmDirective, 
    NgxPaginationModule,
    UserFormModalComponent
  ],
  templateUrl: './utilisateurs.component.html',
  styles: [`
    .user-management {
      min-height: 100vh;
      animation: fadeIn 0.5s ease-out;
    }
    .search-box {
      border-radius: 12px;
      overflow: hidden;
      box-shadow: 0 4px 15px rgba(139, 69, 19, 0.05);
      background: white;
    }
    .custom-table thead th {
      background: rgba(139, 69, 19, 0.03);
      padding: 15px 20px;
      color: var(--marron-chocolat);
      font-size: 0.8rem;
      text-uppercase: uppercase;
      letter-spacing: 1px;
      border-bottom: 2px solid rgba(139, 69, 19, 0.05);
    }
    .custom-table tbody tr {
      transition: all 0.2s ease;
      cursor: pointer;
    }
    .custom-table tbody tr:hover {
      background: rgba(253, 244, 227, 0.3);
    }
    .avatar-img {
      object-fit: cover;
      box-shadow: 0 4px 10px rgba(0,0,0,0.05);
    }
    .status-indicator {
      position: absolute;
      bottom: 2px;
      right: 2px;
      width: 12px;
      height: 12px;
      border-radius: 50%;
      border: 2px solid white;
    }
    .role-badge {
      font-size: 0.75rem;
      border-radius: 8px;
    }
    .btn-action {
      width: 36px;
      height: 36px;
      border-radius: 10px;
      display: flex;
      align-items: center;
      justify-content: center;
      border: none;
      transition: all 0.2s ease;
      background: white;
      color: var(--marron-chocolat);
      box-shadow: 0 4px 8px rgba(0,0,0,0.05);
    }
    .btn-action:hover {
      transform: translateY(-2px);
      box-shadow: 0 6px 12px rgba(0,0,0,0.1);
    }
    .btn-action.edit:hover { color: #3498db; }
    .btn-action.key:hover { color: #f1c40f; }
    .btn-action.delete:hover { color: #e74c3c; }
    
    .custom-switch {
      width: 2.8em !important;
      height: 1.4em !important;
      cursor: pointer;
    }
    .custom-switch:checked {
      background-color: #2ecc71 !important;
      border-color: #2ecc71 !important;
    }
  `]
})
export class UtilisateursComponent implements OnInit {
  private userService = inject(UserService);
  private notification = inject(NotificationService);

  @ViewChild('userModal') userModal!: UserFormModalComponent;

  users: User[] = [];
  filteredUsers: User[] = [];
  p: number = 1;
  filter = {
    keyword: '',
    role: '',
    status: ''
  };

  ngOnInit() {
    this.loadUsers();
  }

  loadUsers() {
    this.userService.getAll().subscribe({
      next: (users) => {
        this.users = users;
        this.applyFilters();
      }
    });
  }

  applyFilters() {
    if (!this.users || !Array.isArray(this.users)) {
      this.filteredUsers = [];
      return;
    }
    this.filteredUsers = this.users.filter(user => {
      const matchKeyword = !this.filter.keyword || 
        `${user.nom} ${user.prenom} ${user.email} ${user.telephone}`.toLowerCase().includes(this.filter.keyword.toLowerCase());
      const matchRole = !this.filter.role || user.role === this.filter.role;
      const matchStatus = !this.filter.status || user.actif.toString() === this.filter.status;
      
      return matchKeyword && matchRole && matchStatus;
    });
    this.p = 1; // Reset to first page
  }

  resetFilters() {
    this.filter = { keyword: '', role: '', status: '' };
    this.applyFilters();
  }

  toggleUserStatus(user: User) {
    this.userService.toggleActif(user.id).subscribe({
      next: (updatedUser) => {
        user.actif = updatedUser.actif;
        const status = user.actif ? 'activé' : 'désactivé';
        this.notification.success(`Compte de ${user.prenom} ${status}.`, 'Succès');
      },
      error: () => {
        // Revert toggle if error
        user.actif = !user.actif;
      }
    });
  }

  resetPassword(user: User) {
    const newPassword = prompt(`Saisissez le nouveau mot de passe pour ${user.prenom} :`, '123456');
    if (newPassword && newPassword.length >= 6) {
      this.userService.resetPassword(user.id, newPassword).subscribe({
        next: () => {
          this.notification.success(`Mot de passe de ${user.prenom} réinitialisé avec succès.`, 'Succès');
        }
      });
    } else if (newPassword) {
      this.notification.warning('Le mot de passe doit contenir au moins 6 caractères.', 'Attention');
    }
  }

  deleteUser(user: User) {
    this.userService.delete(user.id).subscribe({
      next: () => {
        this.users = this.users.filter(u => u.id !== user.id);
        this.applyFilters();
        this.notification.success('Utilisateur supprimé avec succès.', 'Succès');
      }
    });
  }

  openUserModal(user?: User) {
    this.userModal.open(user);
  }

  editUser(user: User) {
    this.openUserModal(user);
  }
}
