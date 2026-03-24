import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { OrdreProductionService } from '../../../core/services/ordre-production.service';
import { ProduitService } from '../../../core/services/produit.service';
import { AuthService } from '../../../core/services/auth.service';
import { OrdreProduction } from '../../../core/models/ordre-production.model';
import { Produit } from '../../../core/models/produit.model';
import { NotificationService } from '../../../core/services/notification.service';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';
import { ConfirmDirective } from '../../../shared/directives/confirm.directive';
import { NgxPaginationModule } from 'ngx-pagination';

@Component({
  selector: 'app-production',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, EmptyStateComponent, ConfirmDirective, NgxPaginationModule],
  templateUrl: './production.component.html',
  styleUrls: ['./production.component.css']
})
export class ProductionComponent implements OnInit {
  private ordreService = inject(OrdreProductionService);
  private produitService = inject(ProduitService);
  private authService = inject(AuthService);
  private notification = inject(NotificationService);
  private fb = inject(FormBuilder);

  ordres: OrdreProduction[] = [];
  filteredOrdres: OrdreProduction[] = [];
  produits: Produit[] = [];
  filterStatut: string = 'ALL';
  p: number = 1;
  loading = false;
  userRole: string | null = null;
  showModal = false;
  isEditing = false;
  currentOrdreId: number | null = null;
  ordreForm!: FormGroup;
  
  stats = {
    totalCout: 0,
    totalQuantite: 0
  };

  ngOnInit() {
    this.userRole = this.authService.getRole();
    this.loadOrdres();
    this.loadProduits();
    this.initForm();
    this.loadStats();
  }
  
  loadStats() {
    const now = new Date();
    const debut = new Date(now.getFullYear(), now.getMonth(), 1).toISOString().split('T')[0];
    const fin = new Date(now.getFullYear(), now.getMonth() + 1, 0).toISOString().split('T')[0];
    
    this.ordreService.getCoutPeriode(debut, fin).subscribe({
      next: cout => this.stats.totalCout = cout,
      error: (err) => {
        console.warn('Backend stats error (waiting for restart):', err);
        this.stats.totalCout = 0;
      }
    });
  }
  
  initForm() {
    this.ordreForm = this.fb.group({
      produitId: [null, [Validators.required]],
      quantite: [1, [Validators.required, Validators.min(1)]],
      dateDebutPrevue: [new Date().toISOString().substring(0, 10), [Validators.required]],
      notes: ['']
    });
  }

  canEdit(): boolean {
    return ['ADMIN', 'CHEF'].includes(this.userRole || '');
  }

  loadOrdres() {
    this.loading = true;
    this.ordreService.getAll().subscribe({
      next: (data) => {
        this.ordres = data.map(o => ({
          ...o,
          notes: o.notes?.startsWith('null') ? o.notes.replace(/^null\n?/, '') : o.notes
        }));
        console.log('Ordres chargés:', this.ordres.map(o => ({ id: o.id, statut: o.statut })));
        this.applyFilters();
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  loadProduits() {
    this.produitService.getAll().subscribe({
      next: (data) => {
        // Uniquement les produits ayant une recette associée peuvent être produits
        this.produits = (data || []).filter(p => p.recetteId != null);
        console.log('Produits avec recette chargés:', this.produits.length);
      },
      error: (err) => console.error('Erreur chargement produits:', err)
    });
  }
  
  applyFilters() {
    if (this.filterStatut === 'ALL') {
      this.filteredOrdres = this.ordres;
    } else {
      this.filteredOrdres = this.ordres.filter(o => {
        if (this.filterStatut === 'TERMINE') return o.statut === 'TERMINEE';
        if (this.filterStatut === 'PLANIFIE') return o.statut === 'PLANIFIE' || o.statut === 'PLANIFIEE';
        if (this.filterStatut === 'ANNULE') return o.statut === 'ANNULEE';
        return o.statut === this.filterStatut;
      });
    }
    this.p = 1;
  }
  
  openModal(ordre?: OrdreProduction) {
    this.isEditing = !!ordre;
    this.showModal = true;
    if (ordre) {
      this.currentOrdreId = ordre.id;
      this.ordreForm.patchValue({
        produitId: ordre.produitId, // Directly use value
        quantite: ordre.quantite,
        dateDebutPrevue: ordre.dateDebutPrevue,
        notes: ordre.notes
      });
    } else {
      this.currentOrdreId = null;
      this.initForm();
    }
  }
  
  closeModal() {
    this.showModal = false;
  }
  
  saveOrdre() {
    if (this.ordreForm.invalid) return;
    this.loading = true;
    const data = this.ordreForm.value;
    const obs = this.isEditing 
      ? this.ordreService.update(this.currentOrdreId!, data)
      : this.ordreService.create(data);
      
    obs.subscribe({
      next: () => {
        this.notification.success(`Ordre ${this.isEditing ? 'mis à jour' : 'créé'} avec succès`);
        this.loadOrdres();
        this.closeModal();
      },
      error: () => {
        this.loading = false;
      }
    });
  }
  
  deleteOrdre(id: number) {
    this.ordreService.delete(id).subscribe({
      next: () => {
        this.notification.success('Ordre supprimé');
        this.loadOrdres();
      }
    });
  }
  
  dupliquerOrdre(id: number) {
    this.ordreService.dupliquer(id).subscribe({
      next: () => {
        this.notification.success('Ordre dupliqué avec succès');
        this.loadOrdres();
      }
    });
  }
  
  consommerIngredients(id: number) {
    this.ordreService.consommerIngredients(id).subscribe({
      next: () => {
        this.notification.success('Ingrédients consommés');
        this.loadOrdres();
      }
    });
  }

  demarrerOrdre(id: number) {
    const request = {
      dateDebutReelle: new Date().toISOString().split('T')[0],
      notes: "Démarrage production"
    };
    this.ordreService.demarrer(id, request).subscribe({
      next: () => {
        this.notification.success('Production démarrée');
        this.loadOrdres();
        this.loadStats();
      },
      error: () => this.notification.error('Erreur au démarrage')
    });
  }

  terminerOrdre(id: number) {
    const request = {
      dateDebutReelle: new Date().toISOString().split('T')[0], // Backend use same logic for end date
      notes: "Clôture production"
    };
    this.ordreService.terminer(id, request).subscribe({
      next: () => {
        this.notification.success('Production terminée');
        this.loadOrdres();
        this.loadStats();
      },
      error: () => this.notification.error('Erreur à la clôture')
    });
  }

  annulerOrdre(id: number) {
    const raison = prompt('Raison de l\'annulation :');
    if (raison !== null) {
      this.ordreService.annuler(id, raison || 'Annulation manuelle').subscribe({
        next: () => {
          this.notification.warning('Production annulée');
          this.loadOrdres();
        }
      });
    }
  }

  getStatutLabel(statut: string): string {
    switch (statut) {
      case 'PLANIFIE': 
      case 'PLANIFIEE': return 'PLANIFIÉ';
      case 'EN_COURS': return 'EN COURS';
      case 'TERMINEE': return 'TERMINÉ';
      case 'ANNULEE': return 'ANNULÉ';
      default: return statut;
    }
  }

  getStatutClass(statut: string): string {
    return 'status-' + statut.toLowerCase();
  }
}
