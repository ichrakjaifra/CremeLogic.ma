import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { FournisseurService } from '../../../core/services/fournisseur.service';
import { Fournisseur } from '../../../core/models/fournisseur.model';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';
import { ConfirmDirective } from '../../../shared/directives/confirm.directive';
import { NgxPaginationModule } from 'ngx-pagination';
import { NotificationService } from '../../../core/services/notification.service';

@Component({
  selector: 'app-fournisseurs',
  standalone: true,
  imports: [CommonModule, FormsModule, EmptyStateComponent, ConfirmDirective, NgxPaginationModule],
  templateUrl: './fournisseurs.component.html',
  styles: [`
    .suppliers-page {
      min-height: 100vh;
      animation: fadeIn 0.5s ease-out;
    }
    .search-box {
      border-radius: 15px;
      overflow: hidden;
      box-shadow: 0 6px 20px rgba(139, 69, 19, 0.08);
      background: white;
    }
    .supplier-card {
      transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
      cursor: pointer;
      border: 1px solid rgba(255, 255, 255, 0.3);
    }
    .supplier-card:hover { 
      transform: translateY(-8px); 
      box-shadow: 0 15px 35px rgba(139, 69, 19, 0.1) !important;
    }
    .supplier-icon { 
      width: 55px; 
      height: 55px; 
      border-radius: 16px; 
      display: flex; 
      align-items: center; 
      justify-content: center; 
      font-size: 1.6rem;
      box-shadow: 0 6px 15px rgba(139, 69, 19, 0.1);
    }
    .bg-chocolate-light { background: #FDF4E3; }
    
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
export class FournisseursComponent implements OnInit {
  private supplierService = inject(FournisseurService);
  private notification = inject(NotificationService);

  fournisseurs: Fournisseur[] = [];
  filteredFournisseurs: Fournisseur[] = [];
  searchTerm: string = '';
  p: number = 1;

  get activeCount(): number {
    return this.fournisseurs.filter(s => s.actif).length;
  }

  ngOnInit() {
    this.loadFournisseurs();
  }

  loadFournisseurs() {
    this.supplierService.getAll().subscribe(data => {
      this.fournisseurs = data;
      this.applySearch();
    });
  }

  applySearch() {
    this.filteredFournisseurs = this.fournisseurs.filter(s => 
      s.nom.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
      (s.ville && s.ville.toLowerCase().includes(this.searchTerm.toLowerCase()))
    );
    this.p = 1;
  }

  openSupplierModal(s?: Fournisseur) {
    this.notification.info('Gestion détaillée des fournisseurs en cours de développement.', 'Info');
  }

  editSupplier(s: Fournisseur) {
    this.openSupplierModal(s);
  }

  toggleStatus(s: Fournisseur) {
    this.supplierService.toggleActif(s.id).subscribe({
      next: () => {
        s.actif = !s.actif;
        this.notification.success(`Statut de ${s.nom} mis à jour.`, 'Succès');
      }
    });
  }
}
