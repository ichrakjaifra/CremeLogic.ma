import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { FournisseurService } from '../../../core/services/fournisseur.service';
import { Fournisseur } from '../../../core/models/fournisseur.model';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';
import { ConfirmDirective } from '../../../shared/directives/confirm.directive';
import { NgxPaginationModule } from 'ngx-pagination';
import { NotificationService } from '../../../core/services/notification.service';

import { FournisseurFormModalComponent } from './fournisseur-form-modal/fournisseur-form-modal.component';
import { FournisseurEvaluationModalComponent } from './fournisseur-evaluation-modal/fournisseur-evaluation-modal.component';
import { ViewChild } from '@angular/core';

@Component({
  selector: 'app-fournisseurs',
  standalone: true,
  imports: [
    CommonModule, 
    FormsModule, 
    EmptyStateComponent, 
    ConfirmDirective, 
    NgxPaginationModule,
    FournisseurFormModalComponent,
    FournisseurEvaluationModalComponent
  ],
  templateUrl: './fournisseurs.component.html',
  styleUrls: ['./fournisseurs.component.css']
})
export class FournisseursComponent implements OnInit {
  private supplierService = inject(FournisseurService);
  private notification = inject(NotificationService);

  @ViewChild('supplierModal') supplierModal!: FournisseurFormModalComponent;
  @ViewChild('evaluationModal') evaluationModal!: FournisseurEvaluationModalComponent;

  fournisseurs: Fournisseur[] = [];
  filteredFournisseurs: Fournisseur[] = [];
  cities: string[] = [];
  searchTerm: string = '';
  selectedCity: string = '';
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
      this.extractCities();
      this.applySearch();
    });
  }

  extractCities() {
    const citySet = new Set<string>();
    this.fournisseurs.forEach(s => {
      if (s.ville) citySet.add(s.ville);
    });
    this.cities = Array.from(citySet).sort();
  }

  applySearch() {
    if (!this.fournisseurs || !Array.isArray(this.fournisseurs)) {
      this.filteredFournisseurs = [];
      return;
    }
    this.filteredFournisseurs = this.fournisseurs.filter(s => {
      const matchesSearch = s.nom.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
                          (s.email && s.email.toLowerCase().includes(this.searchTerm.toLowerCase()));
      const matchesCity = !this.selectedCity || s.ville === this.selectedCity;
      return matchesSearch && matchesCity;
    });
    this.p = 1;
  }

  openSupplierModal(s?: Fournisseur) {
    this.supplierModal.open(s);
  }

  openEvaluationModal(s: Fournisseur) {
    this.evaluationModal.open(s);
  }

  editSupplier(s: Fournisseur) {
    this.openSupplierModal(s);
  }

  deleteSupplier(id: number) {
    this.supplierService.delete(id).subscribe({
      next: () => {
        this.notification.success('Fournisseur supprimé avec succès.', 'Succès');
        this.loadFournisseurs();
      },
      error: (err) => {
        const errorMsg = err.error?.message || 'Erreur lors de la suppression. Vérifiez si des commandes y sont liées.';
        this.notification.error(errorMsg, 'Erreur');
      }
    });
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
