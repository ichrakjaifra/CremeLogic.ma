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
  selectedVente: Vente | null = null;

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
    this.selectedVente = v;
  }

  printInvoice(id: number) {
    // If we're printing from the modal, we already have the vente
    if (this.selectedVente && this.selectedVente.id === id) {
      this.doPrint();
    } else {
      const vente = this.ventes.find(v => v.id === id);
      if (vente) {
        this.selectedVente = vente;
        setTimeout(() => this.doPrint(), 100);
      }
    }
  }

  private doPrint() {
    const printContent = document.getElementById('print-section');
    if (!printContent) return;

    const windowPrint = window.open('', '', 'left=0,top=0,width=800,height=900,toolbar=0,scrollbars=0,status=0');
    if (windowPrint) {
      windowPrint.document.write(`
        <html>
          <head>
            <title>Facture - ${this.selectedVente?.numeroVente}</title>
            <style>
              body { font-family: sans-serif; }
              @media print {
                .no-print { display: none; }
              }
            </style>
          </head>
          <body>
            ${printContent.innerHTML}
          </body>
        </html>
      `);
      windowPrint.document.close();
      windowPrint.focus();
      setTimeout(() => {
        windowPrint.print();
        windowPrint.close();
      }, 500);
    }
  }
}
