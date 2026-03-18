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
  styleUrls: ['./fournisseurs.component.css']
})
export class FournisseursComponent implements OnInit {
  private supplierService = inject(FournisseurService);
  private notification = inject(NotificationService);

  fournisseurs: Fournisseur[] = [];
  filteredFournisseurs: Fournisseur[] = [];
  searchTerm: string = '';
  p: number = 1;

  get activeCount(): number {
    return (this.fournisseurs && Array.isArray(this.fournisseurs)) 
      ? this.fournisseurs.filter(s => s.actif).length 
      : 0;
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
    if (!this.fournisseurs || !Array.isArray(this.fournisseurs)) {
      this.filteredFournisseurs = [];
      return;
    }
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
