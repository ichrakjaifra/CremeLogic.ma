import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { VenteService } from '../../../../core/services/vente.service';
import { Vente } from '../../../../core/models/vente.model';
import { NgxPaginationModule } from 'ngx-pagination';
import { FormatPricePipe } from '../../../../shared/pipes/format-price.pipe';
import { NotificationService } from '../../../../core/services/notification.service';
import { ConfirmDirective } from '../../../../shared/directives/confirm.directive';

@Component({
  selector: 'app-vente-history',
  standalone: true,
  imports: [CommonModule, FormsModule, NgxPaginationModule, FormatPricePipe, ConfirmDirective],
  templateUrl: './vente-history.component.html',
  styleUrls: ['./vente-history.component.css']
})
export class VenteHistoryComponent implements OnInit {
  private venteService = inject(VenteService);
  private notification = inject(NotificationService);

  ventes: Vente[] = [];
  filteredVentes: Vente[] = [];
  searchTerm: string = '';
  dateDebut: string = '';
  dateFin: string = '';
  p: number = 1;
  loading = false;

  ngOnInit() {
    this.loadVentes();
  }

  loadVentes() {
    this.loading = true;
    this.venteService.getAll().subscribe({
      next: (data: Vente[]) => {
        this.ventes = data;
        this.applyFilters();
        this.loading = false;
      },
      error: () => {
        this.notification.error('Erreur lors du chargement des ventes', 'Erreur');
        this.loading = false;
      }
    });
  }

  applyFilters() {
    this.filteredVentes = this.ventes.filter(v => {
      const matchSearch = !this.searchTerm || 
                         v.numeroVente.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
                         (v.nomClient && v.nomClient.toLowerCase().includes(this.searchTerm.toLowerCase()));
      return matchSearch;
    });
    this.p = 1;
  }

  filterByDate() {
    if (!this.dateDebut || !this.dateFin) {
      this.loadVentes();
      return;
    }
    this.loading = true;
    this.venteService.getByPeriode(this.dateDebut, this.dateFin).subscribe({
      next: (data: Vente[]) => {
        this.ventes = data;
        this.applyFilters();
        this.loading = false;
      },
      error: () => {
        this.notification.error('Erreur de filtrage par date', 'Erreur');
        this.loading = false;
      }
    });
  }

  annulerVente(id: number) {
    const raison = prompt('Veuillez saisir la raison de l\'annulation :');
    if (raison) {
      this.venteService.annuler(id, raison).subscribe({
        next: () => {
          this.notification.success('Vente annulée avec succès', 'Succès');
          this.loadVentes();
        },
        error: () => {
          this.notification.error('Erreur lors de l\'annulation', 'Erreur');
        }
      });
    }
  }

  viewDetails(v: Vente) {
    this.notification.info(`Détails de la vente ${v.numeroVente} : ${v.lignesVente.length} articles.`, 'Info');
  }

  printInvoice(id: number) {
     this.notification.info('Génération de la facture...', 'Patientez');
     this.venteService.getFacture(id).subscribe({
       next: (data: any) => {
          this.notification.success('Facture prête pour l\'impression', 'Facture');
          console.log('Facture data:', data);
       },
       error: () => this.notification.error('Erreur facture', 'Erreur')
     });
  }
}
